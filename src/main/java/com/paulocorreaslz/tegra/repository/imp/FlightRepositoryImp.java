package com.paulocorreaslz.tegra.repository.imp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.paulocorreaslz.tegra.aux.FlightDateComparator;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.repository.FlightRepository;
import com.paulocorreaslz.tegra.util.Operator;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
@Repository
public class FlightRepositoryImp implements FlightRepository {
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private CSVParser parser;
	
	@Override
	public List<Flight> loadUber() {
		List<Flight> listFlights = new ArrayList<Flight>();
		try {
			InputStream file = new ClassPathResource("uberair.csv").getInputStream();
			Reader in = new InputStreamReader(file);
			parser = new CSVParser(in, CSVFormat.DEFAULT );
			List<CSVRecord> list = parser.getRecords();
			int line = 0, item = 0;
			String numFlight = null, origin = null, destination = null;
			LocalTime timeDeparture = null, timeArrival = null;
			LocalDate dateStart = null;
			BigDecimal price = null;
			Operator operator = null;

			for( CSVRecord row : list ) {
				for( Object value : row ) {
					if (line > 0) {
						if (item == 0) {
							numFlight = value.toString();
						} else if (item == 1) {
							origin = value.toString();
						} else if (item == 2) {
							destination = value.toString();
						} else if (item == 3) {
							dateStart = LocalDate.parse((String) value, dateFormatter);
						} else if (item == 4) {
							timeDeparture = LocalTime.parse(value.toString(), DateTimeFormatter.ISO_TIME);
						} else if (item == 5) {
							timeArrival = LocalTime.parse(value.toString(), DateTimeFormatter.ISO_TIME);
						} else if (item == 6) {
							price = new BigDecimal(Double.parseDouble((String) value));
							operator = Operator.UBERAIR;
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
			e.printStackTrace();
		}
		return listFlights;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Flight> loadPlanes() {
		List<Flight> listFlights = new ArrayList<>();
		InputStream file = null;
		try {
			file = new ClassPathResource("99planes.json").getInputStream();
		} catch (IOException e1) {
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
		String originJsonValue = (String) info.get("origem");
		String destinationJsonValue = (String) info.get("destino");
		LocalDate dateStartJsonValue = LocalDate.parse(info.get("data_saida").toString(), dateFormatter);
		LocalTime timeDepartureJsonValue = LocalTime.parse(info.get("saida").toString(), DateTimeFormatter.ISO_TIME);
		LocalTime timeArrivalJsonValue = LocalTime.parse(info.get("chegada").toString(),  DateTimeFormatter.ISO_TIME);
		Number priceJsonValue = (Number) info.get("valor");
		BigDecimal valorJsonValue = new BigDecimal(priceJsonValue.longValue());
		Flight flight = new Flight(flightJsonValue, originJsonValue, destinationJsonValue, dateStartJsonValue, timeDepartureJsonValue, timeArrivalJsonValue, valorJsonValue, Operator.PLANES);
		return flight;
	}

	@Override
	public List<Flight> findAll() {
		List<Flight> listAllFlights = new ArrayList<Flight>();

		listAllFlights.addAll(this.loadUber());
		listAllFlights.addAll(this.loadPlanes());

		FlightDateComparator comparator = new FlightDateComparator();
		Collections.sort(listAllFlights, comparator);
		
		return listAllFlights;
	}
	
}
