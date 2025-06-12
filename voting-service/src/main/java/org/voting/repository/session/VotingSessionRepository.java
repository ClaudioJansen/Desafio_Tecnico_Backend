package org.voting.repository.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.voting.domain.session.VotingSession;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    boolean existsByAgendaId(Long agendaId);
}
