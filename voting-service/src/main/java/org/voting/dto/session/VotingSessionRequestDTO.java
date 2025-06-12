package org.voting.dto.session;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSessionRequestDTO {

    private Long agendaId;

    @Min(1)
    private Integer durationInMinutes;
}
