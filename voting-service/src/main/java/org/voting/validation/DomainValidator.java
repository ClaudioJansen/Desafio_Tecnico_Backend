package org.voting.validation;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.voting.client.CpfValidationClient;
import org.voting.domain.session.VotingSession;
import org.voting.domain.vote.Vote;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.voting.config.AppConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainValidator {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final CpfValidationClient cpfValidationClient;

    public void validateSessionUniqueness(Long agendaId) {
        if (votingSessionRepository.existsByAgendaId(agendaId)) {
            throw new BusinessException(ERROR_SESSION_ALREADY_EXISTS);
        }
    }

    @NotNull
    public static VotingSession validateIfSessionAlreadyEnd(List<Vote> votes) {
        VotingSession session = votes.get(0).getSession();
        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new BusinessException(ERROR_SESSION_NOT_ENDED);
        }
        return session;
    }

    @Retry(name = "cpfValidation", fallbackMethod = "validateCpfFallback")
    @Cacheable(value = "cpfStatus", key = "#cpf")
    public boolean validateCpf(String cpf) {
        var response = cpfValidationClient.validateCpf(cpf);
        return ABLE_TO_VOTE.equalsIgnoreCase(response.getStatus());
    }

    public boolean validateCpfFallback(String cpf, Exception ex) {
        log.warn("Fallback: error validating CPF [{}]. Assuming it is valid. Error: {}", cpf, ex.getMessage());
        return DEFAULT_FALLBACK;
    }

    public void validateDuplicateVote(Long sessionId, String cpf) {
        if (voteRepository.existsBySessionIdAndCpf(sessionId, cpf)) {
            log.warn("Duplicate vote detected for CPF {} in session {}", cpf, sessionId);
            throw new BusinessException(ERROR_DUPLICATE_VOTE);
        }
    }

    public void validateSessionIsOpen(VotingSession session) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            throw new BusinessException(ERROR_SESSION_CLOSED);
        }
    }

    public void validateAgendaExists(Long agendaId) {
        if (!agendaRepository.existsById(agendaId)) {
            log.error("Agenda not found: {}", agendaId);
            throw new NotFoundException(ERROR_AGENDA_NOT_FOUND);
        }
    }

}
