package br.com.vinicius.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vinicius.model.Ensaio;

public interface EnsaioRepository extends JpaRepository<Ensaio, Long> {

}
