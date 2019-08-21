package com.paulocorreaslz.tegra.repository;

import java.util.List;

import com.paulocorreaslz.tegra.model.Airport;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
public interface AirportRepository {

	List<Airport> findAll();

}
