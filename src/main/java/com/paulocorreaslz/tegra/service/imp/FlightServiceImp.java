package com.paulocorreaslz.tegra.service.imp;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paulocorreaslz.tegra.aux.FlightTimeComparator;
import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.repository.FlightRepository;
import com.paulocorreaslz.tegra.service.AirportService;
import com.paulocorreaslz.tegra.service.FlightService;
import com.paulocorreaslz.tegra.util.FlightSearch;
import com.paulocorreaslz.tegra.util.Graph;

@Service
public class FlightServiceImp implements FlightService {

	@Autowired
	private AirportService airportService;
	
	private FlightRepository flightRepository;

	private Graph graphAll;

	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public FlightResponse findbyOriginDestinatinAndDate(String origin, String destination, String dateSearch) {
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
		
		FlightResponse response = new FlightResponse(airportService.findAirportByInitials(origin), airportService.findAirportByInitials(destination), dateTimeLeave, dateTimeArrival, listGetFlightsFull);
		
		return response;
	}

	
	public List<Flight> selectFlightMidle(String origin, String destination, List<Flight> listGetFlights) {
		List<Flight> newListFlightMidle = new ArrayList<Flight>();
		int control = 0;
		for(Flight flight:listGetFlights) {
			if (!flight.getOrigin().equals(origin) || !flight.getDestination().equals(destination)) {
				if (control+1 == listGetFlights.size()) {
					if (MINUTES.between(listGetFlights.get(control).getTimeArrival(), listGetFlights.get(control+1).getTimeDeparture()) < 720) {
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
		System.out.println("Criando lista de graphos..");
		Graph graphLocal = new Graph();
		
		for (Flight flight: listFlights) {
			System.out.println("Adicionado trecho de graphos.. origem:"+flight.getOrigin()+" destino:"+flight.getDestination());
			graphLocal.addEdge(flight.getOrigin(), flight.getDestination());
		}
		graphAll = graphLocal;
		System.out.println("Finalizado lista de graphos..");
	}

	public List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate){
		List<Flight> listFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlights = new ArrayList<Flight>();
		List<Flight> listReturnFlightsRoutes = new ArrayList<Flight>();
		
		listFlights = flightRepository.findAll();
		
		//sort by time
		FlightTimeComparator comparator = new FlightTimeComparator();
		Collections.sort(listFlights, comparator);
		
		System.out.println("looking for flights on origin: "+ airportOrigin+ " destination:"+airportDestination);
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
		System.out.println("found "+ found +" flights.");		
		
		listGraphs(listReturnFlights);
		flightSearch.addGraph(graphAll);
		System.out.println("Buscar caminhos..");
		List<String> listRoute = flightSearch.run(airportOrigin, airportDestination);
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
