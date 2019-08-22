package org.javaus.usbase.thymeleaf.processor;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class MenuAttributeTagProcessor extends AbstractAttributeTagProcessor {
	
	private static final String NOME_ATRIBUTO = "menu";
	private static final int PRECEDENCIA = 100;
	
	public MenuAttributeTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, NOME_ATRIBUTO, true, PRECEDENCIA, true);
		
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue , IElementTagStructureHandler structureHandler) {
		     
			
		    // Obtendo configuração do contexto,  atributos e tags do Thymeleaf  
			IEngineConfiguration configuration = context.getConfiguration();
			IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
			IStandardExpression expression = parser.parseExpression(context, attributeValue);
			
			//Obtem do contexto atual (usbase) a tag menu que usa @Urlexpression do Thymeleaf, na pagina
			String menu = (String) expression.execute(context);
				
			    // obtendo o endereco url digitado no browser
				HttpServletRequest request = ((IWebContext) context).getRequest();
				// obtendo o endereco uri dos menu na tela
				String uri = request.getRequestURI();
				
				//  se o menu esta na uri 
				if(uri.matches(menu)){
					String classesExistentes = tag.getAttributeValue("class");
					
					// insere a classe is-active no item <li> do menu selecionado 
					structureHandler.setAttribute("class", classesExistentes + " is-active");
				}
						
				
						
	}


}
