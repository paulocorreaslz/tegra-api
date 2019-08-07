package com.paulocorreaslz.tegra.controller;

/*
 *
 * Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.model.Voo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

	@SuppressWarnings("unchecked")
	@GetMapping("/planes")
	public List<Voo> getPlanesFlights() throws IOException{

		List<Voo> listVoos = new ArrayList<>();
		
		File file = new ClassPathResource("99planes.json").getFile();

		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(file))
		{
			Object obj = jsonParser.parse(reader);

			JSONArray flightList = (JSONArray) obj;
			//System.out.println(flightList);

			flightList.forEach( flitghsJSon -> { 
					listVoos.add(transformFlightInfo( (JSONObject) flitghsJSon )); 
				}
			);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listVoos;
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/air")
	private List<Airport> loadAirports() throws IOException {

		List<Airport> listAirports = new ArrayList<>();

		File file = new ClassPathResource("aeroportos.json").getFile();

		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(file))
		{
			Object obj = jsonParser.parse(reader);

			JSONArray airportsList = (JSONArray) obj;
			//System.out.println(flightList);

			 //airportsList.forEach( airportsJSon -> transformAirportsInfo( (JSONObject) airportsJSon ) );
			airportsList.forEach( airportsJSon -> {
				 listAirports.add(transformAirportsInfo( (JSONObject) airportsJSon ));
				}
			);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return listAirports;

	}

	private Voo transformFlightInfo(JSONObject info)
	{

		String vooJsonValue = (String) info.get("voo");
		System.out.println("voo:"+vooJsonValue);

		String origemJsonValue = (String) info.get("origem");
		System.out.println("origem:"+origemJsonValue);

		String destinoJsonValue = (String) info.get("destino");
		System.out.println("destino:"+destinoJsonValue);

		String dataJsonValue = (String) info.get("data_saida");
		System.out.println("data:"+dataJsonValue);

		String saidaJsonValue = (String) info.get("saida");
		System.out.println("saida:"+saidaJsonValue);

		String chegadaJsonValue = (String) info.get("chegada");
		System.out.println("chegada:"+chegadaJsonValue);

		Number valorJsonValue = (Number) info.get("valor");
		System.out.println("data:"+valorJsonValue);

		System.out.println("----------------------------------");
		Voo voo = new Voo(vooJsonValue, origemJsonValue, destinoJsonValue, dataJsonValue, saidaJsonValue, chegadaJsonValue);
		return voo;
	}

	private Airport transformAirportsInfo(JSONObject info)
	{

		String nomeJsonValue = (String) info.get("nome");
		System.out.println("Nome:"+nomeJsonValue);

		String aeroportoJsonValue = (String) info.get("aeroporto");
		System.out.println("Aeroporto:"+aeroportoJsonValue);


		String cidadeJsonValue = (String) info.get("cidade");
		System.out.println("Cidade:"+cidadeJsonValue);

		System.out.println("----------------------------------");
		
		Airport air = new Airport(nomeJsonValue, aeroportoJsonValue, cidadeJsonValue);
		return air;	  
	}
}