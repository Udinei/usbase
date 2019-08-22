package org.javaus.usbase.model.validation;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import org.javaus.usbase.model.Cliente;
/**
 * Usado para validação do Cpf/Cnpj - para saber qual atributo esta sendo usado e qual deve ser validado no momento
 *  */
public class ClienteGroupSequenceProvider implements DefaultGroupSequenceProvider<Cliente>{

	@Override
	public List<Class<?>> getValidationGroups(Cliente cliente) {
		List<Class<?>> grupos = new ArrayList<>();
		grupos.add(Cliente.class);
		
		// conforme o tipo da pessoa que esta sendo cadastrada/editada
		if(isPessoaSelecionada(cliente)){
			
			// usa anotação correta contida no atributo tipoPessoa para validar
			grupos.add(cliente.getTipoPessoa().getGrupo());
		}
		
		return grupos;
	}

	private boolean isPessoaSelecionada(Cliente cliente) {
		return cliente != null && cliente.getTipoPessoa() != null;
			
	}
}
	
	


