package org.javaus.usbase.repository.helper.venda;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.javaus.usbase.dto.VendaMes;
import org.javaus.usbase.dto.VendaOrigem;
import org.javaus.usbase.model.Venda;
import org.javaus.usbase.repository.filter.VendaFilter;

public interface VendasQueries {
	
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable);

	public Venda buscarComItens(Long codigo);
	
	public BigDecimal valorTotalNoAno();
	
	public BigDecimal valorTotalNoMes();
	
	public BigDecimal valorTicketMedioNoAno();
	
	public List<VendaMes> totalPorMes();

	public List<VendaOrigem> totalPorOrigem();
	
}
