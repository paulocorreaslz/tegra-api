package com.paulocorreaslz.tegra.controller;

/*
*
* Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
*
*/

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@CrossOrigin(origins = {"http://localhost","*"})
@RestController
@RequestMapping("/api")
public class FlightController {
 
  @GetMapping("/online")
  public String online() {
	  return "online";
  }
  
  @GetMapping("/uber")
	public List<CSVRecord> getUberAirFlights()  {
		List<CSVRecord> dados = null;
		try {
			File file = new ClassPathResource("uberair.csv").getFile();
			System.out.println(file.getAbsolutePath());
			Reader in = new FileReader( file );
	        CSVParser parser = new CSVParser( in, CSVFormat.DEFAULT );
	        List<CSVRecord> list = parser.getRecords();
	        dados = list;
	        for( CSVRecord row : list )
	            for( String entry : row )
	                System.out.println( entry );
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dados;
	}
  
}