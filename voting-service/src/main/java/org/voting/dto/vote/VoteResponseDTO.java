package org.voting.dto.vote;

import lombok.Builder;
import lombok.Getter;
import org.voting.domain.vote.VoteChoice;

@Getter
@Builder
public class VoteResponseDTO {
    private Long voteId;
    private Long sessionId;
    private String cpf;
    private VoteChoice choice;
}
