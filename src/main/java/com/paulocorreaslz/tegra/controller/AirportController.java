package com.paulocorreaslz.tegra.controller;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.response.GenericResponse;
import com.paulocorreaslz.tegra.service.imp.AirportServiceImp;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AirportController {

	@Autowired
	private AirportServiceImp airportService;
	
	@ApiOperation(value = "Método para buscar aeroportos disponiveis", response = Iterable.class, tags = "listar aeroportos")
	@GetMapping("/airports")
	public ResponseEntity<GenericResponse<List<Airport>>> loadAirports() throws IOException {
		
		return airportService.findAll();
	}
	
	@ApiOperation(value = "Método para busca aeroporto por sigla", response = Airport.class, tags = "listar aeroporto por sigla")
	@GetMapping("/airport/{initial}")
	public Airport loadAirports(@PathVariable("initial") String initials) throws IOException {
		
		return airportService.findAirportByInitials(initials);
	}
	
	
}
