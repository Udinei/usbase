package org.javaus.usbase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.javaus.usbase.model.Venda;
import org.javaus.usbase.repository.helper.venda.VendasQueries;

public interface Vendas extends JpaRepository<Venda, Long>, VendasQueries {

     
}
