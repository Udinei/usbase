package org.javaus.usbase.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

/**
 * 
 * @author Udinei
 * Essa classe é um annotation  que pode ser aplicada em um atributo de classe, em um metodo, e em outra annotation
 * será executada em tempo de execução
 */

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE}) // onde pode ser aplicada
@Retention(RetentionPolicy.RUNTIME)                                            // em tempo de execução
@Constraint(validatedBy = {})
@Documented          
@Pattern(regexp = "([a-zA-Z]{2}\\d{4})?") // [a-zA-Z] permite caracteres de a à z maiusculo e minusculo,{2} até dois caracter. \\d Numeros até  {4} quatro digitos numericos. "?" e só valida se o campo tiver preenchido
public @interface SKU {
	
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "{org.javaus.constraints.SKU.message}";
	
	Class<?>[] groups() default {};                  // para pode agrupar validação   
	Class<? extends Payload>[] payload() default {}; // para poder acessar mais informação da annotation
	
}
