package org.javaus.usbase.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.model.Estilo;
import org.javaus.usbase.repository.Cervejas;
import org.javaus.usbase.service.exception.ImpossivelExcluirEntidadeException;
import org.javaus.usbase.service.exception.SkuCervejaJaCadastradaException;
import org.javaus.usbase.storage.FotoStorage;
import org.javaus.usbase.util.MessagesUtil;

@Service
public class CadastroCervejaService {
	
	@Autowired
	MessagesUtil messagesUtil;
	
	@Autowired
	private Cervejas cervejas;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja){
		
	Optional<Cerveja> cervejaExistente = cervejas.findBySkuIgnoreCase(cerveja.getSku());
		
		// objeto com o atributo pesquisado existe, e seu identificador é igual ao do objeto a salvar   
		if(cervejaExistente.isPresent() && !cervejaExistente.get().equals(cerveja)){
			throw new SkuCervejaJaCadastradaException(messagesUtil.getMessage("msg.error.atrib.ent.ja.cadastrada.m", "SKU", "cerveja"));
		}
		
		cervejas.save(cerveja);
	}
	
	
	@Transactional
	public void excluir(Cerveja cerveja){
		try{
			String foto = cerveja.getFoto();
			cervejas.delete(cerveja);
			
			cervejas.flush(); // executa a exclusao no banco 
			fotoStorage.excluir(foto); // apagando a foto e thumbnail da foto
			
		}catch(PersistenceException e){
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Esta sendo usada em alguma venda.");
			
		}
		
	}
}
