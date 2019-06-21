package org.javaus.usbase.controller.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.javaus.usbase.service.exception.NomeEstiloJaCadastradoException;

/** Classe observer para tratamento de Exception da aplicacao */
@ControllerAdvice  
public class ControllerAdviceExceptionHandler {
	
	
	// qualquer Exception do tipo NomeEstiloJaCadastradoException sera tratado por esse metodo
	@ExceptionHandler(NomeEstiloJaCadastradoException.class)
	public ResponseEntity<String> handleNomeEstiloJaCadastradoException(NomeEstiloJaCadastradoException e){
		return ResponseEntity.badRequest().body(e.getMessage());
		
	}
	

}
