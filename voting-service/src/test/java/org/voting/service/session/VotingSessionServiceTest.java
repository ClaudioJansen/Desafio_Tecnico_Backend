package org.voting.service.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.voting.domain.agenda.Agenda;
import org.voting.domain.session.VotingSession;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.validation.DomainValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VotingSessionServiceTest {

    private VotingSessionRepository votingSessionRepository;
    private AgendaRepository agendaRepository;
    private DomainValidator domainValidator;
    private VotingSessionService votingSessionService;

    @BeforeEach
    void setUp() {
        votingSessionRepository = mock(VotingSessionRepository.class);
        agendaRepository = mock(AgendaRepository.class);
        domainValidator = mock(DomainValidator.class);
        votingSessionService = new VotingSessionService(votingSessionRepository, agendaRepository, domainValidator);
    }

    @Test
    void shouldOpenSessionWithCustomDuration() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta 1").build();
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        VotingSessionRequestDTO request = VotingSessionRequestDTO.builder()
                .agendaId(1L)
                .durationInMinutes(10)
                .build();

        VotingSessionResponseDTO response = votingSessionService.openSession(request);

        assertNotNull(response);
        assertEquals(1L, response.getAgendaId());
        assertNotNull(response.getStartTime());
        assertEquals(response.getStartTime().plusMinutes(10), response.getEndTime());

        verify(domainValidator).validateSessionUniqueness(1L);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    @Test
    void shouldOpenSessionWithDefaultDurationWhenNull() {
        Agenda agenda = Agenda.builder().id(2L).title("Pauta 2").build();
        when(agendaRepository.findById(2L)).thenReturn(Optional.of(agenda));

        VotingSessionRequestDTO request = VotingSessionRequestDTO.builder()
                .agendaId(2L)
                .durationInMinutes(null)
                .build();

        VotingSessionResponseDTO response = votingSessionService.openSession(request);

        assertEquals(2L, response.getAgendaId());
        assertNotNull(response.getStartTime());
        assertEquals(response.getStartTime().plusMinutes(1), response.getEndTime());

        verify(domainValidator).validateSessionUniqueness(2L);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    @Test
    void shouldThrowNotFoundWhenAgendaDoesNotExist() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        VotingSessionRequestDTO request = VotingSessionRequestDTO.builder()
                .agendaId(99L)
                .durationInMinutes(5)
                .build();

        assertThrows(NotFoundException.class, () -> votingSessionService.openSession(request));
        verify(votingSessionRepository, never()).save(any());
    }
}
