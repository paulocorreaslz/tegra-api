package com.paulocorreaslz.tegra.service.imp;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.paulocorreaslz.tegra.aux.FlightTimeComparator;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.repository.FlightRepository;
import com.paulocorreaslz.tegra.response.GenericResponse;
import com.paulocorreaslz.tegra.service.AirportService;
import com.paulocorreaslz.tegra.service.FlightService;
import com.paulocorreaslz.tegra.util.FlightSearch;
import com.paulocorreaslz.tegra.util.Graph;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
@Service
public class FlightServiceImp implements FlightService {

	@Autowired
	private AirportService airportService;
	
	@Autowired
	private FlightRepository flightRepository;

	private Graph graphAll;

	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public ResponseEntity<GenericResponse<FlightResponse>> findbyOriginDestinatinAndDate(String origin, String destination, String dateSearch) {

		List<Flight> listGetFlights = new ArrayList<Flight>();
		List<Flight> listGetFlightsMidle = new ArrayList<Flight>();
		List<Flight> listGetFlightsDirect = new ArrayList<Flight>();
		List<Flight> listGetFlightsFull = new ArrayList<Flight>();
		
		LocalDate dateFlight = LocalDate.parse((String) dateSearch, dateFormatter);
		LocalDateTime dateTimeInicial = LocalDateTime.of(dateFlight,LocalTime.now());
		GenericResponse<FlightResponse> response = new GenericResponse<>();
		FlightResponse flightResponse = null;
		try {
			listGetFlights = this.getFlightsFromOriginDestination(origin, destination, dateFlight);	
			if (!listGetFlights.isEmpty()) {
				LocalDateTime dateTimeLeave = LocalDateTime.of(dateFlight, listGetFlights.get(0).getTimeDeparture()); 
				LocalDateTime dateTimeArrival = LocalDateTime.of(dateFlight, listGetFlights.get(listGetFlights.size()-1).getTimeArrival());
				
				listGetFlightsMidle = selectFlightMidle(origin, destination, listGetFlights);
				listGetFlightsDirect = selectFlightDirect(origin, destination, listGetFlightsDirect);
				
				listGetFlightsFull.addAll(listGetFlightsDirect);
				listGetFlightsFull.addAll(listGetFlightsMidle);
				
				flightResponse = new FlightResponse(airportService.findAirportByInitials(origin), airportService.findAirportByInitials(destination), dateTimeLeave, dateTimeArrival, listGetFlightsFull);
				response.setData(flightResponse);
			} 
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			flightResponse = new FlightResponse(airportService.findAirportByInitials(origin), airportService.findAirportByInitials(destination), dateTimeInicial, dateTimeInicial,listGetFlights);
		}
		
		if (flightResponse == null || flightResponse.getScales().isEmpty()) {
            response.getErrors().add("99");
            response.getErrors().add("Nenhum trecho encontrado.");
            return ResponseEntity.ok().body(response);
        }

        response.setData(flightResponse);
        return ResponseEntity.ok(response);	
	}
	
	public ResponseEntity<GenericResponse<List<Flight>>> findUber(){
		
		List<Flight> flightListUber = flightRepository.loadUber();
		GenericResponse<List<Flight>> response = new GenericResponse<>();
		response.setData(flightListUber);
		
		if (flightListUber.isEmpty()) {
            response.getErrors().add("Nenhum voo encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(flightListUber);
        return ResponseEntity.ok(response);	
		
	}

	public ResponseEntity<GenericResponse<List<Flight>>> findPlanes(){
		List<Flight> flightListPlanes = flightRepository.loadPlanes();
		GenericResponse<List<Flight>> response = new GenericResponse<>();
		response.setData(flightListPlanes);
		
		if (flightListPlanes.isEmpty()) {
            response.getErrors().add("Nenhum voo encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(flightListPlanes);
        return ResponseEntity.ok(response);
	}
	
	public List<Flight> selectFlightMidle(String origin, String destination, List<Flight> listGetFlights) {
		List<Flight> newListFlightMidle = new ArrayList<Flight>();
		int control = 0;
		for(Flight flight:listGetFlights) {
			if (!flight.getOrigin().equals(origin) || !flight.getDestination().equals(destination)) {
				if (control+1 == listGetFlights.size()) {
					if (MINUTES.between(listGetFlights.get(control).getTimeArrival(), listGetFlights.get(listGetFlights.size()-1).getTimeDeparture()) < 720) {
						newListFlightMidle.add(flight);
					}
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
	
	public List<Flight> selectFlightDirect(String origin, String destination, List<Flight> listGetFlights) {
		List<Flight> newListFlightDirect = new ArrayList<Flight>();
		for(Flight flight:listGetFlights) {
			if (flight.getOrigin().equals(origin) || flight.getDestination().equals(destination)) {
				newListFlightDirect.add(flight);
			}
		}
		return newListFlightDirect;
	}

	private void listGraphs(List<Flight> listFlights) {
		Graph graphLocal = new Graph();
		
		for (Flight flight: listFlights) {
			graphLocal.addEdge(flight.getOrigin(), flight.getDestination());
		}
		graphAll = graphLocal;
	}

	public List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate){
		List<Flight> listFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlightsRoutes = new ArrayList<Flight>();
		
		listFlights = flightRepository.findAll();
		
		//sort by time
		FlightTimeComparator comparator = new FlightTimeComparator();
		Collections.sort(listFlights, comparator);
		
		int found = 0;
		FlightSearch flightSearch = new FlightSearch();
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
		if (found != 0) {
			listGraphs(listReturnFlights);
			flightSearch.addGraph(graphAll);
			List<String> listRoute = flightSearch.run(airportOrigin, airportDestination);
		
			listReturnFlightsRoutes = listRoutesFlight(listRoute,airportOrigin,listReturnFlights,flightDate);
			
		}
		return listReturnFlightsRoutes;
	}
	
	private List<Flight> listRoutesFlight(List<String> route,String origin, List<Flight> listFlightsLimited, LocalDate flightDate){
		List<Flight> listRouteFlights = new ArrayList<Flight>();
		List<Flight> flightsFound = new ArrayList<Flight>();
	
		int controle = 0;
		System.out.println("Tamanho da rota:"+route.size());
		for (String part : route) {
			if (controle+1 < route.size()) {
				flightsFound = findFlightByOriginDestinarionDate(listFlightsLimited, route.get(controle), route.get(controle+1), flightDate);
				listRouteFlights.addAll(flightsFound);
			}
			controle++;
		}
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
			}
		}
		return flightsFound;
	}

	@Override
	public ResponseEntity<GenericResponse<List<Flight>>> findAll() {
		
		List<Flight> flightListAll = flightRepository.findAll();
		GenericResponse<List<Flight>> response = new GenericResponse<>();
		response.setData(flightListAll);
		
		if (flightListAll.isEmpty()) {
            response.getErrors().add("99");
            response.getErrors().add("Nenhum voo encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(flightListAll);
        return ResponseEntity.ok(response);	
		
	}

}
