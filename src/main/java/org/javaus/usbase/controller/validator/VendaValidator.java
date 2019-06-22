package org.javaus.usbase.controller.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javaus.usbase.model.ItemVenda;
import org.javaus.usbase.model.Venda;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class VendaValidator implements Validator {

	/** Declara suporte de validação para a classe Venda */
	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	
	/** target vira como objeto da classe que esta sendo validada */
	@Override
	public void validate(Object target, Errors errors) {
		
		// se não foi informado nenhum cliente na caixa de pesquisa de cliente
		ValidationUtils.rejectIfEmpty(errors,"cliente.codigo", "", "Selecione um cliente na pesquisa rápida");
		
		// convertendo target para Venda
		Venda venda = (Venda) target;
		validarSeInformouApenasHorarioEntrega(errors, venda);
		validarSeInformouItens(errors, venda);
		validarSeValorTotalHeNegativo(errors, venda);
		validaSeAoSalvarExisteProdutoEmEstoque(errors, venda);
		
 	}


	private void validaSeAoSalvarExisteProdutoEmEstoque(Errors errors, Venda venda) {
		List<ItemVenda> list = new ArrayList<ItemVenda>();
		
		if(venda.getItens() != null){
		
			list =  venda.getItens();
			
			list.forEach(p -> {
				                 if ((p.getCerveja().getQuantidadeEstoque() - p.getQuantidade()) < 0){
 				                          errors.reject("", "Venda maior que a quantidade em estoque! "
				                                           +"   Estoque produto: " + p.getCerveja().getNome() + " Quantidade: " + 
				                                           + p.getCerveja().getQuantidadeEstoque());
                        			}
			
			                   });
			
						
		}
	}


	private void validarSeValorTotalHeNegativo(Errors errors, Venda venda) {
		if(venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0){
			errors.reject("", "Valor total não pode ser negativo");
		}
	}

	private void validarSeInformouItens(Errors errors, Venda venda) {
		if(venda.getItens().isEmpty()){
			errors.reject("", "Adicione pelo menos uma cerveja na venda");
		}
	}

	private void validarSeInformouApenasHorarioEntrega(Errors errors, Venda venda) {
		if(venda.getHorarioEntrega() != null && venda.getDataEntrega() == null){
			errors.rejectValue("dataEntrega", "", "Informe uma data de entrega para um horário");
		}
	}
	
}
