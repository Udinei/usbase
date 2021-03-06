package org.javaus.usbase.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


/**
 * Essa classe pode ser injetada em qualquer outra classe model, pois esta anotada com @Embeddable
 * nao é necessario implementar hashCode(), equals(), e nem id 
 * */
@Embeddable
public class Endereco implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String logradouro;  
	private Integer numero;
	private String complemento;
	private String cep; 

	
	@ManyToOne
	@JoinColumn(name="codigo_cidade")
	private Cidade cidade;
	
	@Transient
	private Estado estado;
	
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	public Estado getEstado() {
		return estado;
	}

	
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getNomeCidadeSiglaEstado(){
		if(this.cidade != null) {
			return this.cidade.getNome() + "/" + cidade.getEstado().getSigla(); 
		}
		
		return null;
	}
	
}
