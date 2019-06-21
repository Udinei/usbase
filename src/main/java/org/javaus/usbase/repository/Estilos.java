package org.javaus.usbase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Estilo;
import org.javaus.usbase.repository.helper.estilo.EstilosQueries;

@Repository
public interface Estilos extends JpaRepository<Estilo, Long>, EstilosQueries {
 
	public Optional<Estilo> findByNomeIgnoreCase(String nome);
}
