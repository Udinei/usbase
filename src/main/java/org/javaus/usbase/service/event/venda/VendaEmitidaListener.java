package org.javaus.usbase.service.event.venda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.model.ItemVenda;
import org.javaus.usbase.model.Venda;
import org.javaus.usbase.repository.Cervejas;


/** */
@Component
public class VendaEmitidaListener extends VendaEvent{

	@Autowired
	private Cervejas cervejas;
	
	public VendaEmitidaListener() {
			
	}

	
	public VendaEmitidaListener(Venda venda) {
		super(venda);
	
	}

	@EventListener //caso essa anotação não for usada no metodo, esse metodo não sera executado
	public void atualizaEstoqueVendaEmitida(VendaEmitidaListener vendaEvent){
		
		// para cada item da venda atualiza o estoque
		for(ItemVenda item : vendaEvent.getVenda().getItens()){
				Cerveja cerveja = cervejas.getOne(item.getCerveja().getCodigo());
				
				// subtrai do total do estoque a quantidade vendida do item
				cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() - item.getQuantidade());
				cervejas.save(cerveja);
		}
	}
	
	
}
