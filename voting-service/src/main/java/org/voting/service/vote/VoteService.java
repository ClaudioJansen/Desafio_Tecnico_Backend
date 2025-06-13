package org.voting.service.vote;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.voting.domain.vote.Vote;
import org.voting.domain.vote.VoteChoice;
import org.voting.domain.session.VotingSession;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.NotFoundException;
import org.voting.kafka.VotingResultProducer;
import org.voting.repository.session.VotingSessionRepository;
import org.voting.repository.vote.VoteRepository;
import org.voting.validation.DomainValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.voting.config.AppConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;
    private final VotingResultProducer votingResultProducer;
    private final ObjectMapper objectMapper;
    private final DomainValidator domainValidator;

    public VoteResponseDTO castVote(VoteRequestDTO request) {
        log.info("Receiving vote from CPF [{}] in session [{}]", request.getCpf(), request.getSessionId());

        domainValidator.validateDuplicateVote(request.getSessionId(), request.getCpf());

        if (domainValidator.validateCpf(request.getCpf())) {
            VotingSession session = findSessionOrThrow(request.getSessionId());
            domainValidator.validateSessionIsOpen(session);

            Vote vote = buildVote(request, session);
            voteRepository.save(vote);

            log.info("Vote successfully recorded for CPF [{}] in session [{}]", vote.getCpf(), session.getId());

            return VoteResponseDTO.builder()
                    .voteId(vote.getId())
                    .sessionId(session.getId())
                    .cpf(vote.getCpf())
                    .choice(vote.getChoice())
                    .build();
        }

        log.warn("CPF [{}] is not allowed to vote", request.getCpf());
        throw new BusinessException(ERROR_CPF_UNABLE_TO_VOTE);
    }

    public VoteResultResponseDTO getResultByAgenda(Long agendaId) {
        log.info("Retrieving voting result for agenda ID: {}", agendaId);

        domainValidator.validateAgendaExists(agendaId);

        List<Vote> votes = voteRepository.findBySession_Agenda_Id(agendaId);

        VotingSession session = DomainValidator.validateIfSessionAlreadyEnd(votes);

        long yesCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.YES).count();
        long noCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.NO).count();

        log.info("Result - YES: {}, NO: {}", yesCount, noCount);

        VoteResultResponseDTO resultDTO = VoteResultResponseDTO.builder()
                .agendaId(agendaId)
                .yesVotes((int) yesCount)
                .noVotes((int) noCount)
                .build();

        sendResultToKafka(session, resultDTO);

        return resultDTO;
    }

    private void sendResultToKafka(VotingSession session, VoteResultResponseDTO resultDTO) {
        if (!session.isResultPublished()) {
            CompletableFuture.runAsync(() -> {
                try {
                    String json = objectMapper.writeValueAsString(resultDTO);
                    votingResultProducer.sendVotingResult(json);
                    session.setResultPublished(true);
                    sessionRepository.save(session);
                    log.info("Voting result for agenda {} sent to Kafka", session.getAgenda().getId());
                } catch (Exception e) {
                    log.error("Failed to send voting result to Kafka", e);
                    throw new BusinessException(ERROR_KAFKA_CONNECT);
                }
            });
        }
    }

    private static Vote buildVote(VoteRequestDTO request, VotingSession session) {
        return Vote.builder()
                .cpf(request.getCpf())
                .choice(request.getChoice())
                .session(session)
                .build();
    }

    private VotingSession findSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Voting session not found: {}", sessionId);
                    return new NotFoundException(ERROR_SESSION_NOT_FOUND);
                });
    }

}
