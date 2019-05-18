package org.javaus.usbase.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.javaus.usbase.model.Cidade;
import org.javaus.usbase.repository.filter.CidadeFilter;

public interface CidadesQueries {
	
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);

	public Cidade buscarComEstados(Long codigo);

	
}
