package com.paulocorreaslz.tegra.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.response.GenericResponse;

public interface AirportService {

    ResponseEntity<GenericResponse<List<Airport>>> findAll();

}
