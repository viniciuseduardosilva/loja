package br.com.vinicius.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vinicius.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	boolean existsByEmail(String email);

	Optional<Cliente> findByEmail(String email);

}
