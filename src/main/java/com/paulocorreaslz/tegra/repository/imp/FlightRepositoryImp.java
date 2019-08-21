package com.paulocorreaslz.tegra.repository.imp;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.paulocorreaslz.tegra.aux.FlightDateComparator;
import com.paulocorreaslz.tegra.aux.FlightTimeComparator;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.repository.FlightRepository;
import com.paulocorreaslz.tegra.service.AirportService;
import com.paulocorreaslz.tegra.util.FlightSearch;
import com.paulocorreaslz.tegra.util.Graph;
import com.paulocorreaslz.tegra.util.Operator;

@Repository
public class FlightRepositoryImp implements FlightRepository {
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public List<Flight> loadUber() {
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

	@Override
	public List<Flight> loadPlanes() {
		List<Flight> listFlights = new ArrayList<>();
		InputStream file = null;
		try {
			file = new ClassPathResource("99planes.json").getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONParser jsonParser = new JSONParser();
		try (Reader reader = new InputStreamReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray flightList = (JSONArray) obj;

			flightList.forEach( 
					flitghsJSon -> { 
								listFlights.add(load( (JSONObject) flitghsJSon ));
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

	private Flight load(JSONObject info)
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

	@Override
	public List<Flight> findAll() {
		List<Flight> listAllFlights = new ArrayList<Flight>();

		listAllFlights.addAll(this.loadUber());
		listAllFlights.addAll(this.loadPlanes());

		// sort by date
		FlightDateComparator comparator = new FlightDateComparator();
		Collections.sort(listAllFlights, comparator);
		
		return listAllFlights;
	}
	
}
