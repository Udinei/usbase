package org.javaus.usbase.model;

import org.javaus.usbase.model.validation.CnpjGroup;
import org.javaus.usbase.model.validation.group.CpfGroup;

public enum TipoPessoa {
	
	FISICA("Física", "CPF", "000.000.000-00", CpfGroup.class ) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
		}
	},
	
	JURIDICA("Jurídica", "CNPJ","00.000.000/0000-00", CnpjGroup.class) {
		@Override
		public String formatar(String cpfOuCnpj) {
			return cpfOuCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})", "$1.$2.$3/$4-");
		}
	};

	private String descricao;
	private String documento;
	private String mascara;
	private Class<?> grupo;
	
	TipoPessoa(String descricao, String documento, String mascara, Class<?> grupo){
		 this.descricao = descricao;
		 this.documento = documento;
		 this.mascara = mascara;
		 this.grupo = grupo;
	}
	
	// A criaçao desse metodo abstrato obriga a ser implementado em cada elemento do ENUM o metodo
	public abstract String formatar(String cpfOuCnpj);

	public String getDescricao() {
		return descricao;
	}

	public String getDocumento() {
		return documento;
	}

	public String getMascara() {
		return mascara;
	}

	public Class<?> getGrupo() {
		return grupo;
	}

	
	public static String removerFormatacao(String cpfOuCnpj){
		return cpfOuCnpj.replaceAll("\\.|-|/","");
	}

	
	
}
