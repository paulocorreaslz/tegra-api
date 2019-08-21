package com.paulocorreaslz.tegra.repository;

import java.util.List;

import org.json.simple.JSONObject;

import com.paulocorreaslz.tegra.model.Airport;

public interface AirportRepository {

	List<Airport> findAll();

	Airport load(JSONObject info);
	
	Airport findAirportByInitials(String initials);
}
