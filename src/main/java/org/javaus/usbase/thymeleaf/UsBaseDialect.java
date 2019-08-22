package org.javaus.usbase.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.javaus.usbase.thymeleaf.processor.ClassForErrorAttributeTagProcessor;
import org.javaus.usbase.thymeleaf.processor.MenuAttributeTagProcessor;
import org.javaus.usbase.thymeleaf.processor.MessageElementTagProcessor;
import org.javaus.usbase.thymeleaf.processor.OrderElementTagProcessor;
import org.javaus.usbase.thymeleaf.processor.PaginacaoElementTagProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

/** Essa classe cria os dialetos(xmlns:usbase) que serao usados no html
 * os dialetos criados devem ser adicionados na templateEngine do tymeleaf
 * na classe  Webconfig como um bean e injetados com @Bean */
@Component
public class UsBaseDialect extends AbstractProcessorDialect{

	
	public UsBaseDialect() {
	    super("Javaus UsBase", "usbase", StandardDialect.PROCESSOR_PRECEDENCE);
	}
	
	
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processadores = new HashSet<>();
		processadores.add(new ClassForErrorAttributeTagProcessor(dialectPrefix));
		processadores.add(new MessageElementTagProcessor(dialectPrefix));
		processadores.add(new OrderElementTagProcessor(dialectPrefix));
		processadores.add(new PaginacaoElementTagProcessor(dialectPrefix));
		processadores.add(new MenuAttributeTagProcessor(dialectPrefix));
		return processadores;
	}

}
