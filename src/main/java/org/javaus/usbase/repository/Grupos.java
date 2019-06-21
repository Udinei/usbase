package org.javaus.usbase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Grupo;

@Repository 
public interface Grupos extends JpaRepository<Grupo, Long> {

}
