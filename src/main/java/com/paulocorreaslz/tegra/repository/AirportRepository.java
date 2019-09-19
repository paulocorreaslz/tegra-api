package com.paulocorreaslz.tegra.repository;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
import java.util.List;

import com.paulocorreaslz.tegra.model.Airport;

public interface AirportRepository {

	List<Airport> findAll();

}
