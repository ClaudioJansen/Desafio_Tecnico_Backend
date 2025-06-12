package org.voting.service.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.session.VotingSession;
import org.voting.domain.vote.Vote;
import org.voting.domain.vote.VoteChoice;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.voting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;
    private final AgendaRepository agendaRepository;

    public VoteResponseDTO castVote(VoteRequestDTO request) {
        if (voteRepository.existsBySessionIdAndCpf(request.getSessionId(), request.getCpf())) {
            throw new BusinessException(ERROR_DUPLICATE_VOTE);
        }

        VotingSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new NotFoundException(ERROR_SESSION_NOT_FOUND));

        if (LocalDateTime.now().isBefore(session.getStartTime()) || LocalDateTime.now().isAfter(session.getEndTime())) {
            throw new BusinessException(ERROR_SESSION_CLOSED);
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

    public VoteResultResponseDTO getResultByAgenda(Long agendaId) {
        if (agendaNotExist(agendaId)) {
            throw new NotFoundException(ERROR_AGENDA_NOT_FOUND);
        }

        List<Vote> votes = voteRepository.findBySession_Agenda_Id(agendaId);

        long yesCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.YES).count();
        long noCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.NO).count();

        return VoteResultResponseDTO.builder()
                .agendaId(agendaId)
                .yesVotes((int) yesCount)
                .noVotes((int) noCount)
                .build();
    }

    private boolean agendaNotExist(Long agendaId) {
        return !agendaRepository.existsById(agendaId);
    }
}
