package io.github.vagnereix.agenda.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.vagnereix.agenda.model.entity.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Integer> {
	
}
