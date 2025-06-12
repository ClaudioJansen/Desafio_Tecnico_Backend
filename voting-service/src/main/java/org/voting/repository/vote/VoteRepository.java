package org.voting.repository.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.voting.domain.vote.Vote;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsBySessionIdAndCpf(Long sessionId, String cpf);

    List<Vote> findBySession_Agenda_Id(Long agendaId);
}
