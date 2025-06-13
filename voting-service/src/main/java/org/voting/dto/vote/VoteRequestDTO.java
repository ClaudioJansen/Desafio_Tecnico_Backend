package org.voting.dto.vote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.voting.domain.vote.VoteChoice;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {

    @NotNull(message = "Session ID must not be null")
    private Long sessionId;

    @NotBlank(message = "CPF must not be blank")
    private String cpf;

    @NotNull(message = "Vote choice must not be null")
    private VoteChoice choice;
}
