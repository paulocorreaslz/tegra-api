package com.paulocorreaslz.tegra.repository;

import java.time.LocalDate;
import java.util.List;

import org.json.simple.JSONObject;

import com.paulocorreaslz.tegra.model.Flight;

public interface FlightRepository {

	List<Flight> findbyOriginDestinatinAndDate(String origin, String destination, String dateSearch);
	
	List<Flight> findAll();
	
	List<Flight> loadUber();
	
	List<Flight> loadPlanes();
	
	Flight load(JSONObject info); 
	
	List<Flight> selectFlightDirect(String origin, String destination, List<Flight> listGetFlights);
	
	List<Flight> selectFlightMidle(String origin, String destination, List<Flight> listGetFlights);
	
	List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate);
	

}
