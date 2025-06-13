package org.voting.controller.agenda;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voting.dto.agenda.AgendaRequestDTO;
import org.voting.dto.agenda.AgendaResponseDTO;
import org.voting.service.agenda.AgendaService;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Tag(name = "Agenda", description = "Endpoints for managing agendas")
public class AgendaController {

    private final AgendaService agendaService;

    @Operation(
            summary = "Create a new agenda",
            description = "Registers a new agenda to be voted on.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Agenda successfully created"),
                    @ApiResponse(responseCode = "500", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody @Valid AgendaRequestDTO dto) {
        AgendaResponseDTO response = agendaService.createAgenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
