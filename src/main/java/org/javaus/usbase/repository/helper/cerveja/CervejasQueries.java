package org.javaus.usbase.repository.helper.cerveja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.javaus.usbase.dto.CervejaDTO;
import org.javaus.usbase.dto.ValorItensEstoque;
import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.repository.filter.CervejaFilter;

public interface CervejasQueries {

	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable);
	
	public List<CervejaDTO> porSkuOuNome(String skuOuNome);
	
	public ValorItensEstoque valorItensEstoque();
}
