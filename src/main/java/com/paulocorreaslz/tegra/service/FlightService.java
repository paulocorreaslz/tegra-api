package com.paulocorreaslz.tegra.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.response.GenericResponse;

public interface FlightService {

	ResponseEntity<GenericResponse<FlightResponse>> findbyOriginDestinatinAndDate(String origin, String destination, String dateSearch);
	
	List<Flight> selectFlightDirect(String origin, String destination, List<Flight> listGetFlights);
	
	List<Flight> selectFlightMidle(String origin, String destination, List<Flight> listGetFlights);
	
	List<Flight> getFlightsFromOriginDestination(String airportOrigin, String airportDestination, LocalDate flightDate);

}
