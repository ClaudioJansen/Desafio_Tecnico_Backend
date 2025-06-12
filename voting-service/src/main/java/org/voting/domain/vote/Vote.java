package org.voting.domain.vote;

import jakarta.persistence.*;
import lombok.*;
import org.voting.domain.session.VotingSession;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "cpf"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteChoice choice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private VotingSession session;
}