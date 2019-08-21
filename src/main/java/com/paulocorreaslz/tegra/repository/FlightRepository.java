package com.paulocorreaslz.tegra.repository;

import java.util.List;

import com.paulocorreaslz.tegra.model.Flight;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
public interface FlightRepository {

	List<Flight> findAll();
	
	List<Flight> loadUber();
	
	List<Flight> loadPlanes();
		
}
