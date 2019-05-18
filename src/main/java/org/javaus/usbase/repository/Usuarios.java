package org.javaus.usbase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.javaus.usbase.model.Usuario;
import org.javaus.usbase.repository.helper.usuario.UsuariosQueries;

@Repository
public interface Usuarios extends JpaRepository<Usuario, Long>, UsuariosQueries {
	
	public Optional<Usuario> findByEmail(String email);

	public List<Usuario> findByCodigoIn(Long[] codigos);
	
	
	
}
