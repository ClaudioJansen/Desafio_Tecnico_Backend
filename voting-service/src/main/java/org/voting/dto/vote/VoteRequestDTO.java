package org.voting.dto.vote;

import lombok.Getter;
import lombok.Setter;
import org.voting.domain.vote.VoteChoice;

@Getter
@Setter
public class VoteRequestDTO {
    private Long sessionId;
    private String cpf;
    private VoteChoice choice;
}
