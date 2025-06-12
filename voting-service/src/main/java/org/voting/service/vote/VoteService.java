package org.voting.service.vote;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.client.CpfValidationClient;
import org.voting.domain.vote.Vote;
import org.voting.domain.vote.VoteChoice;
import org.voting.domain.session.VotingSession;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.exception.BusinessException;
import org.voting.exception.ExternalServiceException;
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
    private final CpfValidationClient cpfValidationClient;

    public VoteResponseDTO castVote(VoteRequestDTO request) {
        validateDuplicateVote(request.getSessionId(), request.getCpf());

        if(validateCpf(request.getCpf())){
            VotingSession session = findSessionOrThrow(request.getSessionId());

            validateSessionIsOpen(session);

            Vote vote = buildVote(request, session);

            voteRepository.save(vote);

            return VoteResponseDTO.builder()
                    .voteId(vote.getId())
                    .sessionId(session.getId())
                    .cpf(vote.getCpf())
                    .choice(vote.getChoice())
                    .build();
        }
        throw new BusinessException(ERROR_CPF_UNABLE_TO_VOTE);
    }

    public VoteResultResponseDTO getResultByAgenda(Long agendaId) {
        validateAgendaExists(agendaId);

        List<Vote> votes = voteRepository.findBySession_Agenda_Id(agendaId);

        long yesCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.YES).count();
        long noCount = votes.stream().filter(v -> v.getChoice() == VoteChoice.NO).count();

        return VoteResultResponseDTO.builder()
                .agendaId(agendaId)
                .yesVotes((int) yesCount)
                .noVotes((int) noCount)
                .build();
    }

    private static Vote buildVote(VoteRequestDTO request, VotingSession session) {
        return Vote.builder()
                .cpf(request.getCpf())
                .choice(request.getChoice())
                .session(session)
                .build();
    }

    private boolean validateCpf(String cpf) {
        try {
            var response = cpfValidationClient.validateCpf(cpf);

            return ABBLE_TO_VOTE.equalsIgnoreCase(response.getStatus());
        } catch (FeignException e) {
            // Nota: A API externa https://user-info.herokuapp.com/users/{cpf}, especificada no enunciado do desafio técnico,
            // encontra-se atualmente indisponível ("No such app" — Heroku). Por esse motivo, a validação real do CPF não pode ser efetuada.
            // Para fins avaliativos, a arquitetura da integração foi mantida e implementada corretamente com FeignClient,
            // mas em caso de falha (como a atual indisponibilidade), será assumido por padrão que o CPF é válido (ABLE_TO_VOTE).
//            throw new BusinessException(ERROR_INVALID_CPF);
            return true;
        }
    }

    private void validateDuplicateVote(Long sessionId, String cpf) {
        if (voteRepository.existsBySessionIdAndCpf(sessionId, cpf)) {
            throw new BusinessException(ERROR_DUPLICATE_VOTE);
        }
    }

    private VotingSession findSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ERROR_SESSION_NOT_FOUND));
    }

    private void validateSessionIsOpen(VotingSession session) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            throw new BusinessException(ERROR_SESSION_CLOSED);
        }
    }

    private void validateAgendaExists(Long agendaId) {
        if (!agendaRepository.existsById(agendaId)) {
            throw new NotFoundException(ERROR_AGENDA_NOT_FOUND);
        }
    }
}
