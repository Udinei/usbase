package org.javaus.usbase.dto;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Origem;


/** Essa classe foi criada para recuperar parcialmente os dados da tabela cerveja */
public class CervejaDTO {

	private Long codigo;
	private String sku;
	private String nome;
	private String origem;
	private BigDecimal valor;
	private String foto;
	private String urlThumbnailFoto;
	
	// A origem e um string que vem do banco mas sera convertido para um Enum pelo Spring automaticamente
	public CervejaDTO(Long codigo, String sku, String nome, Origem origem, BigDecimal valor, String foto) {
		this.codigo = codigo;
		this.sku = sku;
		this.nome = nome;
		this.origem = origem.getDescricao(); // obtendo a descrição do Enum origem
		this.valor = valor;
		this.foto = StringUtils.isEmpty(foto) ? "cerveja-mock.png" : foto;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getUrlThumbnailFoto() {
		return urlThumbnailFoto;
	}

	public void setUrlThumbnailFoto(String urlThumbnailFoto) {
		this.urlThumbnailFoto = urlThumbnailFoto;
	}
	
	
	
	
}
