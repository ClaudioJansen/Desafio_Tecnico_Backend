package org.voting.dto.session;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSessionResponseDTO {

    private Long sessionId;
    private Long agendaId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
