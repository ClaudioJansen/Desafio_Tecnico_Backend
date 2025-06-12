package org.voting.controller.agenda;

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
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody @Valid AgendaRequestDTO dto) {
        AgendaResponseDTO response = agendaService.createAgenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
