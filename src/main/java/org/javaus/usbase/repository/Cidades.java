package org.javaus.usbase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Cidade;
import org.javaus.usbase.model.Estado;
import org.javaus.usbase.repository.helper.cidade.CidadesQueries;

@Repository
public interface Cidades extends JpaRepository<Cidade, Long>, CidadesQueries {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);
	
	public Optional<Cidade> findByNomeIgnoreCaseAndEstado(String nome, Estado estado);


	
}
