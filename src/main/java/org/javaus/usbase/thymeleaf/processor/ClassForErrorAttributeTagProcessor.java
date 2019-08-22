package org.javaus.usbase.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

/** Essa classe é o processodor da tag da classforerror */
public class ClassForErrorAttributeTagProcessor extends AbstractAttributeTagProcessor{
	
	 //nome do novo atributo do dialeto usbase, que será usado em tags html para tratamento de msg de erros
	private static final String NOME_ATRIBUTO = "classforerror";
	private static final int PRECEDENCIA = 1000;  // para mais de um atributo qual atributo sera executado primeiro
	
	/** metodo construtor da classe. O parametro que sera passado em String dialectPrefix sera usbase   */
	public ClassForErrorAttributeTagProcessor(String dialectPrefix) {
		// true no final informa que o atributo sera apagado no html gerado
		super(TemplateMode.HTML, dialectPrefix, null, false, NOME_ATRIBUTO, true, PRECEDENCIA, true);
		
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue , IElementTagStructureHandler structureHandler) {
		
				boolean temErro = FieldUtils.hasErrors(context, attributeValue);
				
				if(temErro){
					// pega as classes existentes no html do context
					String classesExistentes = tag.getAttributeValue("class");
					// adiciona a classe de erros do tymeleaf has-error
					structureHandler.setAttribute("class", classesExistentes + " has-error");
				}
			
	}
		
}
