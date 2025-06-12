package org.voting.repository.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.voting.domain.vote.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsBySessionIdAndCpf(Long sessionId, String cpf);
}
