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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paulocorreaslz.tegra.model.Airport;
import com.paulocorreaslz.tegra.model.Voo;
import com.paulocorreaslz.tegra.util.Operator;

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

	@SuppressWarnings("resource")
	@GetMapping("/uber")
	public List<Voo> getUberFlights() throws java.text.ParseException  {
		List<Voo> listVoos = new ArrayList<Voo>();
		try {
			File file = new ClassPathResource("uberair.csv").getFile();
			System.out.println(file.getAbsolutePath());
			Reader in = new FileReader( file );
			CSVParser parser = new CSVParser( in, CSVFormat.DEFAULT );
			List<CSVRecord> list = parser.getRecords();
			int line = 1;
			int item = 0;
			String numero_voo = null, aeroporto_origem= null, aeroporto_destino= null,  horario_saida= null, horario_chegada= null;
			Date data = null;
			BigDecimal preco= null;
			// item mapping
			// 0 = numero_voo
			// 1 = aeroporto_origem
			// 2 = aeroporto_destino
			// 3 = data 
			// 4 = horario_saida
			// 5 = horario_chegada
			// 6 = preco
			for( CSVRecord row : list ) {
				for( Object valor : row ) {
					if (line > 1) {
						if (item == 0) {
							numero_voo = valor.toString();
							System.out.println("voo: "+valor);
						} else if (item == 1) {
							aeroporto_origem = valor.toString();
							System.out.println("origem: "+valor);
						} else if (item == 2) {
							aeroporto_destino = valor.toString();
							System.out.println("destino: "+valor);
						} else if (item == 3) {
							data = new SimpleDateFormat("yyyy-MM-ss").parse((String) valor);
							System.out.println("data: "+valor);
						} else if (item == 4) {
							horario_saida = valor.toString();
							System.out.println("saida: "+valor);
						} else if (item == 5) {
							horario_chegada = valor.toString();
							System.out.println("chegada: "+valor);
						} else if (item == 6) {
							System.out.println("preco: "+valor);
							preco = new BigDecimal(Double.parseDouble((String) valor));
							System.out.println("Operator:"+Operator.UBERAIR);
							System.out.println("----------------------------------");
						}
					}
					if (item < 6) {
						item++;
					} else {
						Voo voo = new Voo(numero_voo, aeroporto_origem, aeroporto_destino, data, horario_saida, horario_chegada, preco, Operator.UBERAIR);
						listVoos.add(voo);
						item = 0;
					}
				}
				line++;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listVoos;
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/all")
	public List<Voo> getAllFlights() throws IOException, java.text.ParseException{
		List<Voo> listAll = new ArrayList<Voo>();

		listAll.addAll(getPlanesFlights());
		listAll.addAll(getUberFlights());

		return listAll;
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

			flightList.forEach( flitghsJSon -> { 
					try {
						listVoos.add(transformFlightInfo( (JSONObject) flitghsJSon ));
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
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

	private Voo transformFlightInfo(JSONObject info) throws java.text.ParseException
	{

		String numVooJsonValue = (String) info.get("voo");
		System.out.println("voo:"+numVooJsonValue);

		String origemJsonValue = (String) info.get("origem");
		System.out.println("origem:"+origemJsonValue);

		String destinoJsonValue = (String) info.get("destino");
		System.out.println("destino:"+destinoJsonValue);
		
		Date dataJsonValue = new SimpleDateFormat("yyyy-MM-ss").parse((String) info.get("data_saida"));
		System.out.println("data:"+dataJsonValue);

		String saidaJsonValue = (String) info.get("saida");
		System.out.println("saida:"+saidaJsonValue);

		String chegadaJsonValue = (String) info.get("chegada");
		System.out.println("chegada:"+chegadaJsonValue);

		Number valor = (Number) info.get("valor");
		BigDecimal valorJsonValue = new BigDecimal(valor.longValue());
		System.out.println("valor:"+valorJsonValue);

		System.out.println("Operator:"+Operator.PLANES);

		System.out.println("----------------------------------");
		Voo voo = new Voo(numVooJsonValue, origemJsonValue, destinoJsonValue, dataJsonValue, saidaJsonValue, chegadaJsonValue, valorJsonValue, Operator.PLANES);
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

		Airport airport = new Airport(nomeJsonValue, aeroportoJsonValue, cidadeJsonValue);
		return airport;	  
	}
}