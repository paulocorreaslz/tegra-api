package com.paulocorreaslz.tegra.controller;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.datetime.joda.LocalTimeParser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.util.FlightDateComparator;
import com.paulocorreaslz.tegra.util.FlightSearch;
import com.paulocorreaslz.tegra.util.FlightTimeComparator;
import com.paulocorreaslz.tegra.util.Graph;
import com.paulocorreaslz.tegra.util.Operator;
import static java.time.temporal.ChronoUnit.MINUTES;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = {"http://localhost","*"})
@RestController
@RequestMapping("/api")
@Api(value = "FlightController", description = "REST APIs for search flights and airports!")
public class FlightController {

	// 
	//private DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private Graph graphAll;
	
	@ApiOperation(value = "Método para verificar a resposta da aplicação", response = java.lang.String.class, tags = "verificar disponibilidade da aplicação")
	@GetMapping("/online")
	public String online() {
		ZonedDateTime now = ZonedDateTime.now();
		System.out.println(now.toString());
		return "online";
	}

	@ApiOperation(value = "Método para buscar trechos de voos operados pela uberAir", response = Iterable.class, tags = "listar voos operados pela uberair")
	@SuppressWarnings("resource")
	@GetMapping("/uber")
	public List<Flight> getUberFlights() throws java.text.ParseException, NumberFormatException  {
		List<Flight> listFlights = new ArrayList<Flight>();
		try {
			InputStream file = new ClassPathResource("uberair.csv").getInputStream();
			Reader in = new InputStreamReader(file);
			CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT );
			List<CSVRecord> list = parser.getRecords();
			int line = 0, item = 0;
			String numFlight = null, origin = null, destination = null;
			LocalTime timeDeparture = null, timeArrival = null;
			LocalDate dateStart = null;
			BigDecimal price = null;
			Operator operator = null;
			// item mapping
			// 0 = numero_voo
			// 1 = aeroporto_origem
			// 2 = aeroporto_destino
			// 3 = data 
			// 4 = horario_saida
			// 5 = horario_chegada
			// 6 = preco

			for( CSVRecord row : list ) {
				//System.out.println("Linha:"+line);
				for( Object value : row ) {
					if (line > 0) {
						if (item == 0) {
							numFlight = value.toString();
							//System.out.println("voo: "+value);
						} else if (item == 1) {
							origin = value.toString();
							//System.out.println("origem: "+value);
						} else if (item == 2) {
							destination = value.toString();
							//System.out.println("destino: "+value);
						} else if (item == 3) {
							dateStart = LocalDate.parse((String) value, dateFormatter);
							//System.out.println("data: "+value);
						} else if (item == 4) {
							timeDeparture = LocalTime.parse(value.toString(), DateTimeFormatter.ISO_TIME);
							//System.out.println("saida: "+value);
						} else if (item == 5) {
							timeArrival = LocalTime.parse(value.toString(), DateTimeFormatter.ISO_TIME);
							//System.out.println("chegada: "+value);
						} else if (item == 6) {
							//System.out.println("preco: "+value);
							price = new BigDecimal(Double.parseDouble((String) value));
							operator = Operator.UBERAIR;
							//System.out.println("Operator:"+operator);
							//System.out.println("----------------------------------");	
						}
					}
					if (item < 6) {
						item++;
					} else {
						if (line > 0) {
							Flight flight = new Flight(numFlight, origin, destination, dateStart, timeDeparture, timeArrival, price, operator);
							listFlights.add(flight);
						}
						item = 0;
					}
				}
				line++;
			}
			System.out.println("total voos Uber:"+ (line-1));	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listFlights;
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Método para buscar trechos de voos operados pela uberAir e 99Planes", response = Iterable.class, tags = "listar todos os voos")
	@GetMapping("/all")
	public List<Flight> getAllFlights() throws IOException, java.text.ParseException{
		List<Flight> listAllFlights = new ArrayList<Flight>();

		listAllFlights.addAll(getPlanesFlights());
		listAllFlights.addAll(getUberFlights());

		// sort by date
		FlightDateComparator comparator = new FlightDateComparator();
		Collections.sort(listAllFlights, comparator);
		
		return listAllFlights;
	}


	@ApiOperation(value = "Método para buscar trechos de voos operados pela 99planes", response = Iterable.class, tags = "listar voos operados pela 99planes")
	@SuppressWarnings("unchecked")
	@GetMapping("/planes")
	public List<Flight> getPlanesFlights() throws IOException{

		List<Flight> listFlights = new ArrayList<>();
		InputStream file = new ClassPathResource("99planes.json").getInputStream();
		JSONParser jsonParser = new JSONParser();
		try (Reader reader = new InputStreamReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray flightList = (JSONArray) obj;

			flightList.forEach( 
					flitghsJSon -> { 
							try {
								listFlights.add(transformFlightInfo( (JSONObject) flitghsJSon ));
							} catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}
					);
			System.out.println("total voos Planes:"+ flightList.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listFlights;
	}

	@ApiOperation(value = "Método para buscar aeroportos disponiveis", response = Iterable.class, tags = "listar aeroportos")
	@SuppressWarnings("unchecked")
	@GetMapping("/airports")
	public List<Airport> loadAirports() throws IOException {

		List<Airport> listAirports = new ArrayList<>();
		InputStream file = new ClassPathResource("aeroportos.json").getInputStream();
		JSONParser jsonParser = new JSONParser();
		try (Reader reader = new InputStreamReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray airportsList = (JSONArray) obj;
			airportsList.forEach( 
						airportsJSon -> {
							listAirports.add(transformAirportsInfo( (JSONObject) airportsJSon ));
						}
					);
			System.out.println("total de aeroportos:"+ airportsList.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listAirports;
	}

	private Flight transformFlightInfo(JSONObject info) throws java.text.ParseException
	{
		String flightJsonValue = (String) info.get("voo");
		//System.out.println("voo:"+flightJsonValue);

		String originJsonValue = (String) info.get("origem");
		//System.out.println("origem:"+originJsonValue);

		String destinationJsonValue = (String) info.get("destino");
		//System.out.println("destino:"+destinationJsonValue);

		LocalDate dateStartJsonValue = LocalDate.parse(info.get("data_saida").toString(), dateFormatter);
		//System.out.println("data:"+dateStartJsonValue);

		LocalTime timeDepartureJsonValue = LocalTime.parse(info.get("saida").toString(), DateTimeFormatter.ISO_TIME);
		//System.out.println("saida:"+timeDepartureJsonValue);

		LocalTime timeArrivalJsonValue = LocalTime.parse(info.get("chegada").toString(),  DateTimeFormatter.ISO_TIME);
		//System.out.println("chegada:"+timeArrivalJsonValue);

		Number priceJsonValue = (Number) info.get("valor");
		BigDecimal valorJsonValue = new BigDecimal(priceJsonValue.longValue());
		//System.out.println("valor:"+valorJsonValue);

		//System.out.println("Operator:"+Operator.PLANES);

		//System.out.println("----------------------------------");
		Flight flight = new Flight(flightJsonValue, originJsonValue, destinationJsonValue, dateStartJsonValue, timeDepartureJsonValue, timeArrivalJsonValue, valorJsonValue, Operator.PLANES);
		return flight;
	}

	private Airport transformAirportsInfo(JSONObject info)
	{

		String nomeJsonValue = (String) info.get("nome");
		System.out.println("Nome:"+nomeJsonValue);

		String aeroportoJsonValue = (String) info.get("aeroporto");
		System.out.println("Aeroporto:"+aeroportoJsonValue);

		String cidadeJsonValue = (String) info.get("cidade");
		System.out.println("Cidade:"+cidadeJsonValue);
		System.out.println("----------------------------------");

		Airport airport = new Airport(nomeJsonValue, aeroportoJsonValue, cidadeJsonValue);
		return airport;	  
	}

	@ApiOperation(value = "Método para buscar de voos operados pela uberAir e 99planes por origem, destino e data especificos", response = Iterable.class, tags = "buscar voos por origem, destino e data")
	@GetMapping("/search/{origin}/{destination}/{datesearch}")
	public FlightResponse searchFlights(@PathVariable("origin") String origin, @PathVariable("destination") String destination, @PathVariable("datesearch") String dateSearch) throws IOException, java.text.ParseException{
		
		List<Flight> listGetFlights = new ArrayList<Flight>();
		List<Flight> listGetFlightsMidle = new ArrayList<Flight>();
		List<Flight> listGetFlightsDirect = new ArrayList<Flight>();
		List<Flight> listGetFlightsFull = new ArrayList<Flight>();
		
		System.out.println("airport origin get: "+ origin);
		System.out.println("airport destination get: "+ destination);
		System.out.println("date:"+ dateSearch);

		LocalDate dateFlight = LocalDate.parse((String) dateSearch, dateFormatter);
		
		listGetFlights = getFlightsFromOriginDestination(origin, destination, dateFlight);

		LocalDateTime dateTimeLeave = LocalDateTime.of(dateFlight, listGetFlights.get(0).getTimeDeparture()); 
		LocalDateTime dateTimeArrival = LocalDateTime.of(dateFlight, listGetFlights.get(listGetFlights.size()-1).getTimeArrival());
		
		listGetFlightsMidle = selectFlightMidle(origin, destination, listGetFlights);
		listGetFlightsDirect = selectFlightDirect(origin, destination, listGetFlightsDirect);
		listGetFlightsFull.addAll(listGetFlightsDirect);
		listGetFlightsFull.addAll(listGetFlightsMidle);
		
		FlightResponse response = new FlightResponse(origin, destination, dateTimeLeave, dateTimeArrival, listGetFlightsFull);
		
		return response;
	}
	
	private List<Flight> selectFlightMidle(String origin, String destination, List<Flight> listGetFlights) {
		List<Flight> newListFlightMidle = new ArrayList<Flight>();
		int control = 0;
		for(Flight flight:listGetFlights) {
			if (!flight.getOrigin().equals(origin) || !flight.getDestination().equals(destination)) {
				if (control+1 == listGetFlights.size()) {
					newListFlightMidle.add(flight);
				} else {
					if (MINUTES.between(listGetFlights.get(control).getTimeArrival(), listGetFlights.get(control+1).getTimeDeparture()) < 720) {
						newListFlightMidle.add(flight);
					}
				}
			}
			control++;
		}
		return newListFlightMidle;
	}
	
	private List<Flight> selectFlightDirect(String origin, String destination, List<Flight> listGetFlights) {
		List<Flight> newListFlightDirect = new ArrayList<Flight>();
		for(Flight flight:listGetFlights) {
			if (flight.getOrigin().equals(origin) || flight.getDestination().equals(destination)) {
				newListFlightDirect.add(flight);
			}
		}
		return newListFlightDirect;
	}

	private void listGraphs(List<Flight> listFlights) {
		System.out.println("Criando lista de graphos..");
		Graph graphLocal = new Graph();
		
		for (Flight flight: listFlights) {
			System.out.println("Adicionado trecho de graphos.. origem:"+flight.getOrigin()+" destino:"+flight.getDestination());
			graphLocal.addEdge(flight.getOrigin(), flight.getDestination());
		}
		graphAll = graphLocal;
		System.out.println("Finalizado lista de graphos..");
	}

	private List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate) throws IOException, java.text.ParseException{
		List<Flight> listFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlightsRoutes = new ArrayList<Flight>();
		
		listFlights = getAllFlights();
		
		//sort by time
		FlightTimeComparator comparator = new FlightTimeComparator();
		Collections.sort(listFlights, comparator);
		
		System.out.println("looking for flights on origin: "+ airportOrigin+ " destination:"+airportDestination);
		int found = 0;
		FlightSearch s = new FlightSearch();
		for (Flight flight: listFlights) {
			if (flight.getOrigin() != null) {
				if ((flight.getOrigin().equals(airportOrigin) 
						|| flight.getDestination().equals(airportDestination)) 
						&& flight.getDateStart().equals(flightDate)) {
							System.out.println("Flight found...");
							listReturnFlights.add(flight);
							found++;
				}
			}
		}
		System.out.println("found "+ found +" flights.");		
		
		listGraphs(listReturnFlights);
		s.addGraph(graphAll);
		System.out.println("Buscar caminhos..");
		List<String> listRoute = s.run(airportOrigin, airportDestination);
		System.out.println("Lista processada de trechos da rota:"+listRoute.toString());
		System.out.println("Fim de busca de graphos..");
		
		listReturnFlightsRoutes = listRoutesFlight(listRoute,airportOrigin,listReturnFlights,flightDate);
		return listReturnFlightsRoutes;
	}
	
	private List<Flight> listRoutesFlight(List<String> route,String origin, List<Flight> listFlightsLimited, LocalDate flightDate){
		List<Flight> listRouteFlights = new ArrayList<Flight>();
		List<Flight> flightsFound = new ArrayList<Flight>();
	
		int controle = 0;
		System.out.println("Tamanho da rota:"+route.size());
		for (String part : route) {
			System.out.println("Iteração da rota. ControllerRoute:"+controle);
			System.out.println("Inserindo em list de rota backup o valor:"+part);
			if (controle+1 < route.size()) {
				flightsFound = findFlightByOriginDestinarionDate(listFlightsLimited, route.get(controle), route.get(controle+1), flightDate);
				listRouteFlights.addAll(flightsFound);
			}
			controle++;
		}
		System.out.println("Total de voos apos busca de rotas:"+listRouteFlights.size());
		return listRouteFlights;
	}
	
	private List<Flight> findFlightByOriginDestinarionDate(List<Flight> listFlightsLimited, String origin, String destination, LocalDate flightDate) {
		List<Flight> flightsFound = new ArrayList<Flight>();
		Flight flight = null;
		for (Flight flightPromisse: listFlightsLimited) {
			if (flightPromisse.getOrigin().equals((String) origin)
					&& flightPromisse.getDestination().equals((String) destination ) 
					&& flightPromisse.getDateStart().isEqual(flightDate)){
				flight = flightPromisse;
				flightsFound.add(flight);
				System.out.println("Localizado o voo de findFlightByOriginDestinarionDate."+flight.toString());
			}
		}
		return flightsFound;
	}
}