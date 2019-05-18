package org.javaus.usbase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Cerveja;
import org.javaus.usbase.repository.helper.cerveja.CervejasQueries;

@Repository
public interface Cervejas extends JpaRepository<Cerveja, Long>, CervejasQueries {

	public Optional<Cerveja> findBySkuIgnoreCase(String sku);

	
}
