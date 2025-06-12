package org.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.voting.domain.entity.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
