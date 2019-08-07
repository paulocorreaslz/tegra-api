/**
 * Criado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
package com.paulocorreaslz.tegra.model;

/**
 * @author Paulo Correa <pauloyaco@gmail.com> - 2019
 *
 */

public class Airport {
	
	private String nome;
	private String aeroporto;
	private String cidade;
	
	public Airport(String nome, String aeroporto, String cidade) {
		this.nome = nome;
		this.aeroporto = aeroporto;
		this.cidade = cidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getAeroporto() {
		return aeroporto;
	}

	public void setAeroporto(String aeroporto) {
		this.aeroporto = aeroporto;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	
	
}
