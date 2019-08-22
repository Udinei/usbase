package org.javaus.usbase.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import org.javaus.usbase.validation.AtributoConfirmacao;
/**
 * Object - Permite que essa anotação possa ser utilizada para validar qualquer classe
 * */
public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object>{

	private String atributo;
	private String atributoConfirmacao;
	
	
	@Override
	public void initialize(AtributoConfirmacao constraintAnotation) {
		this.atributo = constraintAnotation.atributo();
		this.atributoConfirmacao = constraintAnotation.atributoConfirmacao();
		
	}
	

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean valido = false;
		
		try {
			Object valorAtributo = BeanUtils.getProperty(object, this.atributo);
			Object valorAtributoConfirmacao = BeanUtils.getProperty(object, this.atributoConfirmacao);
			
			valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao) || ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro recuperando valores de atributos", e);
		}
	
		// tem erro
		if(!valido){
   			    // evitando a mensagem de validacao duplicada
			    context.disableDefaultConstraintViolation();
				
			    // acessando contexto das mensagem de erro
			    String mensagem = context.getDefaultConstraintMessageTemplate();
				ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
				
				// informa qual atributo esta com erro, para que possa ser aplicado a css de erro (cor vermelho)
				violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}
		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
			return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}


	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}
	

}
