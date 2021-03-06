package com.paulocorreaslz.tegra.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.repository.imp.AirportRepositoryImp;
import com.paulocorreaslz.tegra.response.GenericResponse;
import com.paulocorreaslz.tegra.service.AirportService;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
@Service
public class AirportServiceImp implements AirportService {

	@Autowired
	private AirportRepositoryImp airportRepository;
	
	@Override
	public ResponseEntity<GenericResponse<List<Airport>>> findAll() {
		GenericResponse<List<Airport>> response = new GenericResponse<>();

        List<Airport> airports = this.airportRepository.findAll();
        if (airports.isEmpty()) {
            response.getErrors().add("99");
            response.getErrors().add("Nenhum aeroporto encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(airports);
        return ResponseEntity.ok(response);	
     }

	@Override
	public Airport findAirportByInitials(String initials) {
		Airport response = null;
		List<Airport> airports = this.airportRepository.findAll();

		for(Airport airport:airports) {
			if (airport.getAirport().equals((String) initials)) {
		        response = airport;
			}
		}
        return response;
    }
}
