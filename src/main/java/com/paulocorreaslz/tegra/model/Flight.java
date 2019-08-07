package com.paulocorreaslz.tegra.model;

/*
*
* Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
*
*/

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import com.paulocorreaslz.tegra.util.Operator;

public class Flight implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String numFlight;
	private String origin;
	private String destination;
	private LocalDate dateStart;
	private LocalTime timeDeparture;
	private LocalTime timeArrival;
	private BigDecimal price;
	private Operator operator;
	
	
	public Flight (String numflight, String origin, String destination, LocalDate dateStart, LocalTime timeDeparture, LocalTime timeArrival, BigDecimal price, Operator operator) {
		this.numFlight = numflight;
		this.origin = origin;
		this.destination = destination;
		this.dateStart = dateStart;
		this.timeDeparture = timeDeparture;
		this.timeArrival = timeArrival;
		this.price = price;
		this.operator = operator;
	}

	public String getNumFlight() {
		return numFlight;
	}

	public void setNumFlight(String numFlight) {
		this.numFlight = numFlight;
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

	public LocalDate getDateStart() {
		return dateStart;
	}

	public void setDateStart(LocalDate dateStart) {
		this.dateStart = dateStart;
	}

	public LocalTime getTimeDeparture() {
		return timeDeparture;
	}

	public void setTimeDeparture(LocalTime timeDeparture) {
		this.timeDeparture = timeDeparture;
	}

	public LocalTime getTimeArrival() {
		return timeArrival;
	}

	public void setTimeArrival(LocalTime timeArrival) {
		this.timeArrival = timeArrival;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String toString() {
		return "Flight:["+this.numFlight+","+this.origin+","+this.destination+","+this.dateStart.toString()+","+
				this.timeDeparture.toString()+","+this.timeArrival.toString()+","+this.price+","+this.operator+"]";
		}
}
