package org.voting.service.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.session.VotingSession;
import org.voting.domain.agenda.Agenda;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.voting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;

    public VotingSessionResponseDTO openSession(VotingSessionRequestDTO request) {
        Agenda agenda = findAgendaOrThrow(request.getAgendaId());

        validateSessionUniqueness(request.getAgendaId());

        int duration = Objects.nonNull(request.getDurationInMinutes())
                ? request.getDurationInMinutes()
                : DEFAULT_SESSION_DURATION;

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(duration);

        VotingSession session = buildVotingSession(agenda, start, end);

        votingSessionRepository.save(session);

        return VotingSessionResponseDTO.builder()
                .sessionId(session.getId())
                .agendaId(agenda.getId())
                .startTime(start)
                .endTime(end)
                .build();
    }

    private static VotingSession buildVotingSession(Agenda agenda, LocalDateTime start, LocalDateTime end) {
        return VotingSession.builder()
                .agenda(agenda)
                .startTime(start)
                .endTime(end)
                .build();
    }

    private Agenda findAgendaOrThrow(Long agendaId) {
        return agendaRepository.findById(agendaId)
                .orElseThrow(() -> new NotFoundException(ERROR_AGENDA_NOT_FOUND));
    }

    private void validateSessionUniqueness(Long agendaId) {
        if (votingSessionRepository.existsByAgendaId(agendaId)) {
            throw new BusinessException(ERROR_SESSION_ALREADY_EXISTS);
        }
    }
}
