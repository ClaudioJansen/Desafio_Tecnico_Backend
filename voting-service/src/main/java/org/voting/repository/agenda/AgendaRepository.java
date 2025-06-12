package org.voting.repository.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.voting.domain.agenda.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
