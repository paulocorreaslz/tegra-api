package com.paulocorreaslz.tegra.controller;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.util.Operator;

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
public class FlightController {

	// metodo inicial de teste de aplicação online.
	@GetMapping("/online")
	public String online() {
		return "online";
	}

	@SuppressWarnings("resource")
	@GetMapping("/uber")
	public List<Flight> getUberFlights() throws java.text.ParseException, NumberFormatException  {
		List<Flight> listFlights = new ArrayList<Flight>();
		try {
			File file = new ClassPathResource("uberair.csv").getFile();
			Reader in = new FileReader( file );
			CSVParser parser = new CSVParser( in, CSVFormat.DEFAULT );
			List<CSVRecord> list = parser.getRecords();
			int line = 1, item = 0;
			String numFlight = null, origin = null, destination = null,  timeDeparture = null, timeArrival = null;
			Date dateStart = null;
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
				for( Object value : row ) {
					if (line > 1) {
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
							dateStart = new SimpleDateFormat("yyyy-MM-ss").parse((String) value);
							System.out.println("data: "+value);
						} else if (item == 4) {
							timeDeparture = value.toString();
							System.out.println("saida: "+value);
						} else if (item == 5) {
							timeArrival = value.toString();
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
						Flight flight = new Flight(numFlight, origin, destination, dateStart, timeDeparture, timeArrival, price, operator);
						listFlights.add(flight);
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
	@GetMapping("/all")
	public List<Flight> getAllFlights() throws IOException, java.text.ParseException{
		List<Flight> listAll = new ArrayList<Flight>();

		listAll.addAll(getPlanesFlights());
		listAll.addAll(getUberFlights());

		return listAll;
	}

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
		
		Date dateStartJsonValue = new SimpleDateFormat("yyyy-MM-ss").parse((String) info.get("data_saida"));
		System.out.println("data:"+dateStartJsonValue);

		String timeDepartureJsonValue = (String) info.get("saida");
		System.out.println("saida:"+timeDepartureJsonValue);

		String timeArrivalJsonValue = (String) info.get("chegada");
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
	
	@GetMapping("/search/{airport}/{start}/{end}")
	public List<Flight> searchFlights(@PathVariable("airport") String airport, @PathVariable("start") String start,@PathVariable("end") String end){
		List<Flight> listGetFlights = new ArrayList<Flight>();
			
			System.out.println("airport: "+ airport);
			System.out.println("start: "+start);
			System.out.println("end:"+ end);
			
		return listGetFlights;
	}
}