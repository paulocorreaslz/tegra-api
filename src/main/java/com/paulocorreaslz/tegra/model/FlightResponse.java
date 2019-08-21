/**
 * Criado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
package com.paulocorreaslz.tegra.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */
public class FlightResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(notes = "Origem do voo",name="airportOrigin",required=true,value="airportOrigin")
	private Airport airportOrigin;
	@ApiModelProperty(notes = "destination do voo",name="airportDestination",required=true,value="airportDestination")
	private Airport airportDestination;
	@ApiModelProperty(notes = "Data de departure do voo",name="departure",required=true,value="departure")
	private LocalDateTime departure;
	@ApiModelProperty(notes = "Data de arrival do voo",name="arrival",required=true,value="arrival")
	private LocalDateTime arrival;
	@ApiModelProperty(notes = "Trechos de voo",name="scales",required=true,value="scales")
	private List<Flight> scales;
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
	
	public FlightResponse(Airport origin, Airport destination, LocalDateTime departure, LocalDateTime arrival, List<Flight> scales) {
		this.airportOrigin = origin;
		this.airportDestination = destination;
		this.departure = departure;
		this.arrival = arrival;
		this.scales = scales;
	}

	public Airport getOrigin() {
		return airportOrigin;
	}

	public void setOrigin(Airport origin) {
		this.airportOrigin = origin;
	}

	public Airport getDestination() {
		return airportDestination;
	}

	public void setDestination(Airport destination) {
		this.airportDestination = destination;
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
