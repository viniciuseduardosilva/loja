package br.com.vinicius.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vinicius.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String>{
	
	Optional<Usuario> findByLogin(String email);
	

}
