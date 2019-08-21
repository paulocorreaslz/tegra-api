package com.paulocorreaslz.tegra.repository;

import java.time.LocalDate;
import java.util.List;

import org.json.simple.JSONObject;

import com.paulocorreaslz.tegra.model.Flight;

public interface FlightRepository {

	
	List<Flight> findAll();
	
	List<Flight> loadUber();
	
	List<Flight> loadPlanes();
		
}
