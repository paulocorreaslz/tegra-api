package com.paulocorreaslz.tegra.model;

/*
*
* Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
*
*/

import java.io.Serializable;
import java.time.LocalDate;

public class Voo<T> implements Serializable {
	
	private long id ;
	private String numeroVoo;
	private String origem;
	private String destino;
	private LocalDate data;
	private LocalDate horarioSaida;
	private LocalDate horarioChegada;
	
	public Voo (String numero, String origem, String destino, LocalDate data, LocalDate saida, LocalDate chegada) {
		this.numeroVoo = numero;
		this.origem = origem;
		this.destino = destino;
		this.data = data;
		this.horarioSaida = saida;
		this.horarioChegada = chegada;
	}

	public String getNumeroVoo() {
		return numeroVoo;
	}

	public void setNumeroVoo(String numeroVoo) {
		this.numeroVoo = numeroVoo;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalDate getHorarioSaida() {
		return horarioSaida;
	}

	public void setHorarioSaida(LocalDate horarioSaida) {
		this.horarioSaida = horarioSaida;
	}

	public LocalDate getHorarioChegada() {
		return horarioChegada;
	}

	public void setHorarioChegada(LocalDate horarioChegada) {
		this.horarioChegada = horarioChegada;
	}
	
}
