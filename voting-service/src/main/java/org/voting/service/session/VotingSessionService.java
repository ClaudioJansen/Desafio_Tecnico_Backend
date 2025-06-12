package org.voting.service.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.agenda.Agenda;
import org.voting.domain.session.VotingSession;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;

import java.time.LocalDateTime;

import static org.voting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;

    public VotingSessionResponseDTO openSession(VotingSessionRequestDTO request) {
        Agenda agenda = agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new NotFoundException(ERROR_AGENDA_NOT_FOUND));

        if (votingSessionRepository.existsByAgendaId(request.getAgendaId())) {
            throw new BusinessException(ERROR_SESSION_ALREADY_EXISTS);
        }

        int duration = request.getDurationInMinutes() != null ? request.getDurationInMinutes() : DEFAULT_SESSION_DURATION;

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(duration);

        VotingSession session = VotingSession.builder()
                .agenda(agenda)
                .startTime(start)
                .endTime(end)
                .build();

        votingSessionRepository.save(session);

        return VotingSessionResponseDTO.builder()
                .sessionId(session.getId())
                .agendaId(agenda.getId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }
}
