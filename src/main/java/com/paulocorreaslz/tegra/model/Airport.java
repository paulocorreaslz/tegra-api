package com.paulocorreaslz.tegra.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

public class Airport implements Serializable {
	
	@ApiModelProperty(notes = "Nome do aeroporto",name="name",required=true,value="nome")
	private String name;
	@ApiModelProperty(notes = "sigla do aeroporto",name="airport",required=true,value="sigla")
	private String airport;
	@ApiModelProperty(notes = "cidade do aeroporto",name="city",required=true,value="cidade")
	private String city;
	
	public Airport(String name, String airport, String city) {
		this.name = name;
		this.airport = airport;
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAirport() {
		return airport;
	}

	public void setAirport(String airport) {
		this.airport = airport;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
