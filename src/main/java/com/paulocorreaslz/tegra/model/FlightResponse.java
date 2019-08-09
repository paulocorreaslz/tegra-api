/**
 * Criado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
package com.paulocorreaslz.tegra.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class FlightResponse implements Serializable {
	
	@ApiModelProperty(notes = "Origem do voo",name="origin",required=true,value="origin")
	private String origin;
	@ApiModelProperty(notes = "destination do voo",name="destination",required=true,value="destination")
	private String destination;
	@ApiModelProperty(notes = "Data de departure do voo",name="departure",required=true,value="departure")
	private LocalDateTime departure;
	@ApiModelProperty(notes = "Data de arrival do voo",name="arrival",required=true,value="arrival")
	private LocalDateTime arrival;
	@ApiModelProperty(notes = "Trechos do voo",name="scales",required=true,value="scales")
	private List<Flight> scales;
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
	
	public FlightResponse(String origin, String destination, LocalDateTime departure, LocalDateTime arrival, List<Flight> scales) {
		this.origin = origin;
		this.destination = destination;
		this.departure = departure;
		this.arrival = arrival;
		this.scales = scales;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDeparture() {
		return departure.format(dateFormatter).toString();
	}

	public void setDeparture(LocalDateTime departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival.format(dateFormatter).toString();
	}

	public void setArrival(LocalDateTime arrival) {
		this.arrival = arrival;
	}

	public List<Flight> getScales() {
		return scales;
	}

	public void setScales(List<Flight> scales) {
		this.scales = scales;
	}
	
}
