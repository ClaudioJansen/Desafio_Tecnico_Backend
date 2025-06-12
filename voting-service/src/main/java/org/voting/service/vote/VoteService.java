package org.voting.service.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.session.VotingSession;
import org.voting.domain.vote.Vote;
import org.voting.domain.vote.VoteChoice;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;

    public VoteResponseDTO castVote(VoteRequestDTO request) {
        if (voteRepository.existsBySessionIdAndCpf(request.getSessionId(), request.getCpf())) {
            throw new IllegalStateException("User has already voted in this session.");
        }

        VotingSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Voting session not found."));

        if (LocalDateTime.now().isBefore(session.getStartTime()) || LocalDateTime.now().isAfter(session.getEndTime())) {
            throw new IllegalStateException("Voting session is not open.");
        }

        Vote vote = Vote.builder()
                .cpf(request.getCpf())
                .choice(request.getChoice())
                .session(session)
                .build();

        voteRepository.save(vote);

        return VoteResponseDTO.builder()
                .voteId(vote.getId())
                .sessionId(session.getId())
                .cpf(vote.getCpf())
                .choice(vote.getChoice())
                .build();
    }
}
