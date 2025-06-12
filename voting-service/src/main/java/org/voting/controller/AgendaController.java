package org.voting.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voting.dto.AgendaRequestDTO;
import org.voting.dto.AgendaResponseDTO;
import org.voting.service.AgendaService;

@RestController
@RequestMapping("/api/v1/agendas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody @Valid AgendaRequestDTO dto) {
        AgendaResponseDTO response = agendaService.createAgenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
