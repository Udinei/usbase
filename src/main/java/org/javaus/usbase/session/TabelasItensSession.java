package org.javaus.usbase.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.model.ItemVenda;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Component
public class TabelasItensSession {
	
	private Set<TabelaItensVenda> tabelas = new HashSet<>();

	
	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(cerveja, quantidade);
		tabelas.add(tabela);
	}
	
	
	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		System.out.println(">>>>>UUid "+ uuid +"  quantidade"+ quantidade);
		
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
		
		
	}
	
	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
	}

	public List<ItemVenda> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	
	public BigDecimal getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}
	
	
	private TabelaItensVenda buscarTabelaPorUuid(String uuid){
		TabelaItensVenda tabela = tabelas.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new TabelaItensVenda(uuid));
		
		return tabela;
	}

	public List<ItemVenda> ordenaListaItensPorSku(List<ItemVenda> itens) {
		List<ItemVenda> list = new ArrayList<ItemVenda>();
		
		list =  itens.stream().sorted((i1, i2) -> i1.getCerveja().getSku().compareTo(i2.getCerveja().getSku()))
				  .collect(Collectors.toList());
                   //.forEach(p -> System.out.println(p.getCerveja().getSku()));
	    
	    return list;
	}

	
}
