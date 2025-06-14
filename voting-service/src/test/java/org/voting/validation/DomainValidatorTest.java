package org.voting.validation;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.voting.client.CpfValidationClient;
import org.voting.client.dto.CpfValidationResponse;
import org.voting.domain.session.VotingSession;
import org.voting.domain.vote.Vote;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.repository.agenda.AgendaRepository;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DomainValidatorTest {

    private AgendaRepository agendaRepository;
    private VotingSessionRepository votingSessionRepository;
    private VoteRepository voteRepository;
    private CpfValidationClient cpfValidationClient;
    private DomainValidator validator;

    @BeforeEach
    void setup() {
        agendaRepository = mock(AgendaRepository.class);
        votingSessionRepository = mock(VotingSessionRepository.class);
        voteRepository = mock(VoteRepository.class);
        cpfValidationClient = mock(CpfValidationClient.class);
        validator = new DomainValidator(agendaRepository, votingSessionRepository, voteRepository, cpfValidationClient);
    }

    @Test
    void shouldThrowWhenSessionAlreadyExists() {
        when(votingSessionRepository.existsByAgendaId(1L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> validator.validateSessionUniqueness(1L));
    }

    @Test
    void shouldReturnSessionIfEnded() {
        VotingSession session = VotingSession.builder()
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().minusMinutes(1))
                .build();
        Vote vote = Vote.builder().session(session).build();

        VotingSession result = DomainValidator.validateIfSessionAlreadyEnd(List.of(vote));
        assertNotNull(result);
    }

    @Test
    void shouldThrowIfSessionNotEnded() {
        VotingSession session = VotingSession.builder()
                .startTime(LocalDateTime.now().minusMinutes(1))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .build();
        Vote vote = Vote.builder().session(session).build();

        assertThrows(BusinessException.class, () -> DomainValidator.validateIfSessionAlreadyEnd(List.of(vote)));
    }

    @Test
    void shouldReturnTrueWhenCpfAbleToVote() {
        when(cpfValidationClient.validateCpf("123")).thenReturn(CpfValidationResponse.builder().status("ABLE_TO_VOTE").build());
        boolean result = validator.validateCpf("123");
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenCpfUnableToVote() {
        when(cpfValidationClient.validateCpf("123")).thenReturn(CpfValidationResponse.builder().status("UNABLE_TO_VOTE").build());
        boolean result = validator.validateCpf("123");
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenCpfFailsTwiceAndFallback() {
        FeignException simulatedException = FeignException.errorStatus(
                "GET",
                Response.builder()
                        .status(500)
                        .reason("Internal Server Error")
                        .request(Request.create(Request.HttpMethod.GET, "http://dummy", Map.of(), null, null, null))
                        .build()
        );

        when(cpfValidationClient.validateCpf("12345678900"))
                .thenThrow(simulatedException)
                .thenThrow(simulatedException);

        boolean result = validator.validateCpf("12345678900");

        assertTrue(result);

        verify(cpfValidationClient, times(2)).validateCpf("12345678900");
    }

    @Test
    void shouldThrowWhenVoteAlreadyExists() {
        when(voteRepository.existsBySessionIdAndCpf(1L, "123")).thenReturn(true);
        assertThrows(BusinessException.class, () -> validator.validateDuplicateVote(1L, "123"));
    }

    @Test
    void shouldThrowWhenSessionClosed() {
        VotingSession session = VotingSession.builder()
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .build();
        assertThrows(BusinessException.class, () -> validator.validateSessionIsOpen(session));
    }

    @Test
    void shouldThrowWhenSessionNotStarted() {
        VotingSession session = VotingSession.builder()
                .startTime(LocalDateTime.now().plusMinutes(1))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .build();
        assertThrows(BusinessException.class, () -> validator.validateSessionIsOpen(session));
    }

    @Test
    void shouldThrowWhenAgendaNotExists() {
        when(agendaRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> validator.validateAgendaExists(1L));
    }
}
