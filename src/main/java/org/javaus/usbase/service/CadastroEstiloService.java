package org.javaus.usbase.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.javaus.usbase.model.Estilo;
import org.javaus.usbase.repository.Estilos;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.NomeEstiloJaCadastradoException;
import org.javaus.usbase.util.MessagesUtil;

@Service
public class CadastroEstiloService {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Estilos estilos;
	
	/** Salva ou altera o objeto informado
	    controle de transação manual, necessario para evitar que o spring faça begin end nas consultas tambem
	    retorna estilo com o codigo, para preenchimento da combobox apos o cadastro rapido
	 */
	@Transactional 
	public Estilo salvar(Estilo estilo){
		Optional<Estilo> estiloExistente = estilos.findByNomeIgnoreCase(estilo.getNome());
		
		// objeto com o atributo pesquisado existe, e seu identificador é igual ao do objeto a salvar   
		if(estiloExistente.isPresent() && !estiloExistente.get().equals(estilo)){
			throw new NomeEstiloJaCadastradoException(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrado", "Nome", "estilo")); // entao lanca exceptiono
		}
		
		// salva um novo objeto, ou altera caso identificador do objeto ja existir 
		return estilos.saveAndFlush(estilo);
	}

	
	@Transactional
	public void excluir(Estilo estilo){
		try{
			
			estilos.delete(estilo);
			estilos.flush();
						
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar estilo. Esta sendo usado em alguma cerveja.");
			
		}
		
	}

	
}
