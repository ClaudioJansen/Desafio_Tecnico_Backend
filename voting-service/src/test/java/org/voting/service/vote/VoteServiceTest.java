package org.voting.service.vote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.voting.domain.session.VotingSession;
import org.voting.domain.vote.Vote;
import org.voting.domain.vote.VoteChoice;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.kafka.VotingResultProducer;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;
import org.voting.validation.DomainValidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    private VoteRepository voteRepository;
    private VotingSessionRepository sessionRepository;
    private VotingResultProducer votingResultProducer;
    private DomainValidator domainValidator;
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        voteRepository = mock(VoteRepository.class);
        sessionRepository = mock(VotingSessionRepository.class);
        votingResultProducer = mock(VotingResultProducer.class);
        domainValidator = mock(DomainValidator.class);
        ObjectMapper objectMapper = new ObjectMapper();
        voteService = new VoteService(voteRepository, sessionRepository, votingResultProducer, objectMapper, domainValidator);
    }

    @Test
    void shouldCastVoteSuccessfully() {
        VoteRequestDTO request = VoteRequestDTO.builder()
                .cpf("12345678900")
                .choice(VoteChoice.YES)
                .sessionId(1L)
                .build();

        VotingSession session = VotingSession.builder().id(1L).build();

        when(domainValidator.validateCpf("12345678900")).thenReturn(true);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Vote savedVote = Vote.builder()
                .id(10L)
                .cpf("12345678900")
                .choice(VoteChoice.YES)
                .session(session)
                .build();

        when(voteRepository.save(any(Vote.class))).thenReturn(savedVote);

        VoteResponseDTO response = voteService.castVote(request);

        assertEquals(10L, response.getVoteId());
        assertEquals("12345678900", response.getCpf());
        assertEquals(VoteChoice.YES, response.getChoice());

        verify(domainValidator).validateDuplicateVote(1L, "12345678900");
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCpfIsInvalid() {
        VoteRequestDTO request = VoteRequestDTO.builder()
                .cpf("99999999999")
                .choice(VoteChoice.NO)
                .sessionId(1L)
                .build();

        when(domainValidator.validateCpf("99999999999")).thenReturn(false);

        assertThrows(BusinessException.class, () -> voteService.castVote(request));
    }

    @Test
    void shouldGetVoteResultSuccessfully() {
        Long agendaId = 1L;
        VotingSession session = VotingSession.builder().id(5L).resultPublished(false).endTime(LocalDateTime.now().minusMinutes(1)).build();

        Vote vote1 = Vote.builder().choice(VoteChoice.YES).session(session).build();
        Vote vote2 = Vote.builder().choice(VoteChoice.NO).session(session).build();
        Vote vote3 = Vote.builder().choice(VoteChoice.YES).session(session).build();

        when(voteRepository.findBySession_Agenda_Id(agendaId)).thenReturn(List.of(vote1, vote2, vote3));
        when(sessionRepository.save(any())).thenReturn(session);
        doNothing().when(votingResultProducer).sendVotingResult(any());
        doNothing().when(domainValidator).validateAgendaExists(agendaId);

        VoteResultResponseDTO result = voteService.getResultByAgenda(agendaId);

        assertEquals(agendaId, result.getAgendaId());
        assertEquals(2, result.getYesVotes());
        assertEquals(1, result.getNoVotes());
    }

    @Test
    void shouldThrowNotFoundWhenSessionNotExists() throws Exception {
        when(sessionRepository.findById(42L)).thenReturn(Optional.empty());

        Method method = VoteService.class.getDeclaredMethod("findSessionOrThrow", Long.class);
        method.setAccessible(true);

        try {
            method.invoke(voteService, 42L);
            fail("Expected NotFoundException to be thrown");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof NotFoundException);
            assertEquals("Voting session not found.", cause.getMessage());
        }
    }

}
