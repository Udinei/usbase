package org.javaus.usbase.config.format;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
/**
 * Essa classe faz a conversao de valores para o locale pt-BR, de um Bigdecimal ou de uma string evitando assim
 * o erro abaixo  quando do uso de  intercionalizacao
 * Failed to convert property value of type [java.lang.String] to required type [java.math.BigDecimal] for property valorFrete; 
 * nested exception is java.lang.NumberFormatException
 * 
 * @author Udinei
 *
 */
@Component
public class IntegerFormatter extends NumberFormatter<Integer> {
   
	@Autowired
	private Environment env;
	
	@Override
	public String pattern(Locale locale) {
	
		return env.getProperty("bigdecimal.format", "#,##0");
	}

	

}
