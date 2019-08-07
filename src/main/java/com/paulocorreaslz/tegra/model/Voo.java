package com.paulocorreaslz.tegra.model;

/*
*
* Criado por Paulo Correa <pauloyaco@gmail.com> - 2019
*
*/

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.paulocorreaslz.tegra.util.Company;

public class Voo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String numeroVoo;
	private String origem;
	private String destino;
	private Date data;
	private String horarioSaida;
	private String horarioChegada;
	private BigDecimal preco;
	private Company company;
	
	
	public Voo (String numero, String origem, String destino, Date data, String saida, String chegada, BigDecimal preco, Company company) {
		this.numeroVoo = numero;
		this.origem = origem;
		this.destino = destino;
		this.data = data;
		this.horarioSaida = saida;
		this.horarioChegada = chegada;
		this.preco = preco;
		this.company = company;
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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getHorarioSaida() {
		return horarioSaida;
	}

	public void setHorarioSaida(String horarioSaida) {
		this.horarioSaida = horarioSaida;
	}

	public String getHorarioChegada() {
		return horarioChegada;
	}

	public void setHorarioChegada(String horarioChegada) {
		this.horarioChegada = horarioChegada;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
