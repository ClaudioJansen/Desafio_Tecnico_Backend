package org.voting.service.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.agenda.Agenda;
import org.voting.domain.session.VotingSession;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;

    public VotingSessionResponseDTO openSession(VotingSessionRequestDTO request) {
        Agenda agenda = agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new RuntimeException("Agenda not found"));

        if (votingSessionRepository.existsByAgendaId(request.getAgendaId())) {
            throw new IllegalStateException("A voting session already exists for this agenda.");
        }

        int duration = request.getDurationInMinutes() != null ? request.getDurationInMinutes() : 1;

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
