package org.javaus.usbase.aceitacao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.javaus.usbase.model.Estado;
import org.javaus.usbase.service.CadastroEstadoService;
//import org.javaus.usbase.test.base.UsBaseApplicationDBunitTest;

//public class CadastroEstadoTestandoTest extends UsBaseApplicationDBunitTest {
public class CadastroEstadoTestandoTest { 

	
	@Autowired
	CadastroEstadoService cadastroEstadoService;

	@Test
	public void deveInserirUmNovoEstado() throws Exception {
			
		Estado estado =  new Estado();
		estado.setNome("EstadoTeste");
		estado.setSigla("ET");
		
		estado = cadastroEstadoService.salvar(estado);
		
		System.out.println(">>> " + estado.getCodigo());
		
	}

}
