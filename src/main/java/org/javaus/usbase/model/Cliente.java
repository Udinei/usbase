package org.javaus.usbase.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.javaus.usbase.model.validation.ClienteGroupSequenceProvider;
import org.javaus.usbase.model.validation.CnpjGroup;
import org.javaus.usbase.model.validation.group.CpfGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="cliente")
@DynamicUpdate
@GroupSequenceProvider(ClienteGroupSequenceProvider.class) // Trata a Validação das anotacao @CPF/@CNPJ
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	
	@NotNull(message = "Tipo pessoa é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(name="tipo_pessoa")
	private TipoPessoa tipoPessoa;
	
	// Qual validacoes usar entre @CPF e @CNPJ sera tratada por @GroupSequenceProvider anotada na classe cliente
	@NotBlank(message = "CPF/CNPJ é obrigatório")
	@CPF(groups = CpfGroup.class)
	@CNPJ(groups = CnpjGroup.class)
	@Column(name="cpf_cnpj")
	private String cpfOuCnpj;
	
	private String telefone;
	
	@Email(message= "email inválido")
	private String email;
	
	@JsonIgnore  // iguinore a inicialização do endereco, para chamadas via Ajax do cliente
	@Embedded
	private Endereco endereco;

	// Metodos de callback do JPA - Antes de persistir ou fazer update tira a mascara do cpfOuCnpj
	@PrePersist @PreUpdate
	private void preInsertPreUpdate(){
		this.cpfOuCnpj = TipoPessoa.removerFormatacao(this.cpfOuCnpj);
	}

	public String getCpfOuCnpjSemFormatacao(){
		return TipoPessoa.removerFormatacao(this.cpfOuCnpj);
	}

	
	// @PostLoad - apos carregar o objeto cliente, formata o CPF/CNPJ
	@PostLoad 
	private void postLoad(){
		this.cpfOuCnpj = this.tipoPessoa.formatar(this.cpfOuCnpj);	
	}
	

	
	// novo objeto retorna true  
	public boolean isNovo(){
			return codigo == null;
	}

	
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}



	public TipoPessoa getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoa tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	


	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

	
	
	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Endereco getEndereco() {
			return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	
}
