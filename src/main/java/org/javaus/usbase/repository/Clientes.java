package org.javaus.usbase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Cliente;
import org.javaus.usbase.repository.helper.cliente.ClientesQueries;

@Repository
public interface Clientes extends JpaRepository<Cliente, Long>, ClientesQueries {

	public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj);


	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);


}
