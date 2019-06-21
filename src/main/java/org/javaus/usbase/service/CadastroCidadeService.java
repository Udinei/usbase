package org.javaus.usbase.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.javaus.usbase.model.Cidade;
import org.javaus.usbase.repository.Cidades;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.NomeCidadeJaCadastradoException;
import org.javaus.usbase.util.MessagesUtil;


@Service
public class CadastroCidadeService  {

	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Cidades cidades;
	

	@Transactional
	public void salvar(Cidade cidade){
		//Optional<Cidade> cidadeExistente = cidades.findByNomeIgnoreCase(cidade.getNome());
		Optional<Cidade> cidadeExistente = cidades.findByNomeIgnoreCaseAndEstado(cidade.getNome(), cidade.getEstado());
		
		if(cidadeExistente.isPresent() && !cidadeExistente.get().equals(cidade)){
			//throw new NomeCidadeJaCadastradoException("Nome da cidade já cadastrada");
			throw new NomeCidadeJaCadastradoException(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrada", "Nome", "cidade"));
		}
		
		
		cidades.saveAndFlush(cidade);
	}

	@Transactional
	public void excluir(Cidade cidade) {
		try{
			
			cidades.delete(cidade);
			cidades.flush();
						
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cidade. Esta sendo usado em algum outro cadastro.");
			
		}
		
	}

}
