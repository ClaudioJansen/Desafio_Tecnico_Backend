package org.voting.dto.agenda;

import jakarta.validation.constraints.NotBlank;

public record AgendaRequestDTO(
        @NotBlank String title,
        String description
) {}
