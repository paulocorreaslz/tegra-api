package com.paulocorreaslz.tegra.controller;

import static org.mockito.Matchers.anyString;

/*
 *
 * Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.util.FlightComparator;
import com.paulocorreaslz.tegra.util.Operator;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@CrossOrigin(origins = {"http://localhost","*"})
@RestController
@RequestMapping("/api")
@Api(value = "FlightController", description = "REST APIs for search flights and airports!")
public class FlightController {

	// 
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@ApiOperation(value = "Método para verificar a resposta da aplicação", response = java.lang.String.class, tags = "verificar disponibilidade da aplicação")
	@GetMapping("/online")
	public String online() {
		return "online";
	}

	@ApiOperation(value = "Método para buscar trechos de voos operados pela uberAir", response = Iterable.class, tags = "listar voos operados pela uberair")
	@SuppressWarnings("resource")
	@GetMapping("/uber")
	public List<Flight> getUberFlights() throws java.text.ParseException, NumberFormatException  {
		List<Flight> listFlights = new ArrayList<Flight>();
		try {
			File file = new ClassPathResource("uberair.csv").getFile();
			Reader in = new FileReader( file );
			CSVParser parser = new CSVParser( in, CSVFormat.DEFAULT );
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
				System.out.println("Linha:"+line);
				for( Object value : row ) {
					if (line > 0) {
						if (item == 0) {
							numFlight = value.toString();
							System.out.println("voo: "+value);
						} else if (item == 1) {
							origin = value.toString();
							System.out.println("origem: "+value);
						} else if (item == 2) {
							destination = value.toString();
							System.out.println("destino: "+value);
						} else if (item == 3) {
							dateStart = LocalDate.parse((String) value, dateFormatter);
							System.out.println("data: "+value);
						} else if (item == 4) {
							timeDeparture = LocalTime.parse(value.toString(), timeFormatter);
							System.out.println("saida: "+value);
						} else if (item == 5) {
							timeArrival = LocalTime.parse(value.toString(), timeFormatter);
							System.out.println("chegada: "+value);
						} else if (item == 6) {
							System.out.println("preco: "+value);
							price = new BigDecimal(Double.parseDouble((String) value));
							operator = Operator.UBERAIR;
							System.out.println("Operator:"+operator);
							System.out.println("----------------------------------");	
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
		List<Flight> listAll = new ArrayList<Flight>();

		listAll.addAll(getPlanesFlights());
		listAll.addAll(getUberFlights());
				
		FlightComparator comparator = new FlightComparator();
		Collections.sort(listAll, comparator);
	
		return listAll;
	}

    
	@ApiOperation(value = "Método para buscar trechos de voos operados pela 99planes", response = Iterable.class, tags = "listar voos operados pela 99planes")
	@SuppressWarnings("unchecked")
	@GetMapping("/planes")
	public List<Flight> getPlanesFlights() throws IOException{

		List<Flight> listFlights = new ArrayList<>();
		File file = new ClassPathResource("99planes.json").getFile();
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray flightList = (JSONArray) obj;

			flightList.forEach( flitghsJSon -> { 
					try {
						listFlights.add(transformFlightInfo( (JSONObject) flitghsJSon ));
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			);

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
	private List<Airport> loadAirports() throws IOException {

		List<Airport> listAirports = new ArrayList<>();
		File file = new ClassPathResource("aeroportos.json").getFile();
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray airportsList = (JSONArray) obj;

			airportsList.forEach( airportsJSon -> {
				listAirports.add(transformAirportsInfo( (JSONObject) airportsJSon ));
					}
				);

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
		System.out.println("voo:"+flightJsonValue);

		String originJsonValue = (String) info.get("origem");
		System.out.println("origem:"+originJsonValue);

		String destinationJsonValue = (String) info.get("destino");
		System.out.println("destino:"+destinationJsonValue);
		
		LocalDate dateStartJsonValue = LocalDate.parse((String) info.get("data_saida"), dateFormatter);
		System.out.println("data:"+dateStartJsonValue);

		LocalTime timeDepartureJsonValue = LocalTime.parse((String) info.get("saida"), timeFormatter);
		System.out.println("saida:"+timeDepartureJsonValue);

		LocalTime timeArrivalJsonValue = LocalTime.parse((String) info.get("chegada"), timeFormatter);
		System.out.println("chegada:"+timeArrivalJsonValue);

		Number priceJsonValue = (Number) info.get("valor");
		BigDecimal valorJsonValue = new BigDecimal(priceJsonValue.longValue());
		System.out.println("valor:"+valorJsonValue);

		System.out.println("Operator:"+Operator.PLANES);

		System.out.println("----------------------------------");
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
	public List<Flight> searchFlights(@PathVariable("origin") String origin, @PathVariable("destination") String destination, @PathVariable("datesearch") String dateSearch) throws IOException, java.text.ParseException{
		List<Flight> listGetFlights = new ArrayList<Flight>();
		System.out.println("airport origin get: "+ origin);
		System.out.println("airport destination get: "+ destination);
		System.out.println("date:"+ dateSearch);
		
		LocalDate dateFlight = LocalDate.parse((String) dateSearch, dateFormatter);
		
		listGetFlights = getFlightsFromOriginDestination(origin, destination, dateFlight);
			
		return listGetFlights;
	}
	
	private List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate) throws IOException, java.text.ParseException{
		List<Flight> listFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlights = new ArrayList<Flight>();
		listFlights = getAllFlights();
		
		System.out.println("looking for flights on origin: "+ airportOrigin+ " destination:"+airportDestination);
		int found = 0;
		for (Flight flight: listFlights) {
			if (flight.getOrigin() != null) {
				System.out.println(flight.toString());
				if (flight.getOrigin().equals(airportOrigin) && 
						flight.getDestination().equals(airportDestination) &&
						flight.getDateStart().equals(flightDate)) {
					System.out.println("Flight found...");
					listReturnFlights.add(flight);
					found++;
				}
			}
		}
		System.out.println("found "+ found +" flights.");
		return listReturnFlights;
	}
}