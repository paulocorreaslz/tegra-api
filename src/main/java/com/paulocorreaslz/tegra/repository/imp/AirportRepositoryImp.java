package com.paulocorreaslz.tegra.repository.imp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.repository.AirportRepository;

@Repository
public class AirportRepositoryImp implements AirportRepository {

	@SuppressWarnings("unchecked")
	@Override
	public List<Airport> findAll() {
		List<Airport> listAirports = new ArrayList<>();
		InputStream file = null;
		try {
			file = new ClassPathResource("aeroportos.json").getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONParser jsonParser = new JSONParser();
		try (Reader reader = new InputStreamReader(file))
		{
			Object obj = jsonParser.parse(reader);
			JSONArray airportsList = (JSONArray) obj;
			airportsList.forEach( 
						airportsJSon -> {
							listAirports.add(load( (JSONObject) airportsJSon ));
						}
					);
			System.out.println("total de aeroportos:"+ airportsList.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return listAirports;
	}


	private Airport load(JSONObject info)
	{

		String nomeJsonValue = (String) info.get("nome");
		System.out.println("Nome:"+nomeJsonValue);

		String aeroportoJsonValue = (String) info.get("aeroporto");
		System.out.println("Aeroporto:"+aeroportoJsonValue);

		String cidadeJsonValue = (String) info.get("cidade");
		System.out.println("Cidade:"+cidadeJsonValue);
		System.out.println("----------------------------------");

		Airport airport = new Airport(nomeJsonValue, aeroportoJsonValue, cidadeJsonValue);
		return airport;	  
	}
	
}
