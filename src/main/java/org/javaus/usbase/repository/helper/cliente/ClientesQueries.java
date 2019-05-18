package org.javaus.usbase.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.javaus.usbase.model.Cliente;
import org.javaus.usbase.repository.filter.ClienteFilter;

public interface ClientesQueries {

	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);
	
	public Cliente buscaComEndereco(Long codigo);
}
