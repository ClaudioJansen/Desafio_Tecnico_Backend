package org.voting.domain.session;

import jakarta.persistence.*;
import lombok.*;
import org.voting.domain.agenda.Agenda;

import java.time.LocalDateTime;

@Entity
@Table(name = "voting_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
