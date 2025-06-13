package org.voting.dto.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSessionRequestDTO {

    @NotNull(message = "Agenda ID must not be null")
    private Long agendaId;

    @Min(1)
    private Integer durationInMinutes;
}
