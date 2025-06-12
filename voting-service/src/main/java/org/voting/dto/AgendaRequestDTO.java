package org.voting.dto;

import jakarta.validation.constraints.NotBlank;

public record AgendaRequestDTO(
        @NotBlank String title,
        String description
) {}
