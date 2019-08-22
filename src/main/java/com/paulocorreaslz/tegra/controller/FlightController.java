package com.paulocorreaslz.tegra.controller;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Flight;
import com.paulocorreaslz.tegra.model.FlightResponse;
import com.paulocorreaslz.tegra.response.GenericResponse;
import com.paulocorreaslz.tegra.service.imp.FlightServiceImp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = {"http://localhost","*"})
@RestController
@RequestMapping("/api")
@Api(value = "FlightController", description = "REST APIs for search flights and airports!")
public class FlightController {

	@Autowired
	private FlightServiceImp flightServiceImp;
	
	@ApiOperation(value = "Método para verificar a resposta da aplicação", response = java.lang.String.class, tags = "verificar disponibilidade da aplicação")
	@GetMapping("/online")
	public String online() {
		ZonedDateTime now = ZonedDateTime.now();
		System.out.println(now.toString());
		return "online";
	}

	@ApiOperation(value = "Método para buscar trechos de voos operados pela uberAir", response = Iterable.class, tags = "listar voos operados pela uberair")
	@GetMapping("/uber")
	public ResponseEntity<GenericResponse<List<Flight>>> getUberFlights() throws java.text.ParseException, NumberFormatException  {	
		return flightServiceImp.findUber();
	}

	@ApiOperation(value = "Método para buscar trechos de voos operados pela uberAir e 99Planes", response = Iterable.class, tags = "listar todos os voos")
	@GetMapping("/all")
	public ResponseEntity<GenericResponse<List<Flight>>> getAllFlights(){
		return flightServiceImp.findAll();
	}


	@ApiOperation(value = "Método para buscar trechos de voos operados pela 99planes", response = Iterable.class, tags = "listar voos operados pela 99planes")
	@GetMapping("/planes")
	public ResponseEntity<GenericResponse<List<Flight>>> getPlanesFlights() throws IOException{
		return flightServiceImp.findPlanes();
	}

	@ApiOperation(value = "Método para buscar de voos operados pela uberAir e 99planes por origem, destino e data especificos", response = Iterable.class, tags = "buscar voos por origem, destino e data")
	@GetMapping("/search/{origin}/{destination}/{datesearch}")
	public ResponseEntity<GenericResponse<FlightResponse>> searchFlights(@PathVariable("origin") String origin, @PathVariable("destination") String destination, @PathVariable("datesearch") String dateSearch) throws IOException, java.text.ParseException{
		return flightServiceImp.findbyOriginDestinatinAndDate(origin, destination, dateSearch);
	}
	
}