package org.javaus.usbase.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

public class ClassForErrorAttributeTagProcessor extends AbstractAttributeTagProcessor{
	
	private static final String NOME_ATRIBUTO = "classforerror";
	private static final int PRECEDENCIA = 1000;  // para mais de um atributo qual atributo sera executado primeiro
	
	/** O parametro que sera passado em String dialectPrefix sera usbase
	    criando um atributo novo classforerror */
	public ClassForErrorAttributeTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, NOME_ATRIBUTO, true, PRECEDENCIA, true);
		
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue , IElementTagStructureHandler structureHandler) {
		
				boolean temErro = FieldUtils.hasErrors(context, attributeValue);
				
				if(temErro){
					String classesExistentes = tag.getAttributeValue("class");
					structureHandler.setAttribute("class", classesExistentes + " has-error");
				}
			
	}
		
}
