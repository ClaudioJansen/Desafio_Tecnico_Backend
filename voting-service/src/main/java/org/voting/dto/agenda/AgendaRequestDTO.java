package org.voting.dto.agenda;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AgendaRequestDTO {
    @NotBlank
    private String title;
    private String description;
}
