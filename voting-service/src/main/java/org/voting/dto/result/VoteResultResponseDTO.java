package org.voting.dto.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoteResultResponseDTO {
    private Long agendaId;
    private int yesVotes;
    private int noVotes;
}
