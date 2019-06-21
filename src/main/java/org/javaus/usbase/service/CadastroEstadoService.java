package org.javaus.usbase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.javaus.usbase.model.Estado;
import org.javaus.usbase.repository.Estados;

@Service
public class CadastroEstadoService {
	
	@Autowired
	Estados estados;
	
	@Transactional
	public Estado salvar(Estado estado){
		  return estados.save(estado);
		 
	}

}
