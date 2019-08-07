/**
 * Criado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
package com.paulocorreaslz.tegra.model;

/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

public class Airport {
	
	private String name;
	private String airport;
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
