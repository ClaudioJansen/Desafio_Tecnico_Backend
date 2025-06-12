package org.voting.controller.session;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.service.session.VotingSessionService;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @PostMapping
    public ResponseEntity<VotingSessionResponseDTO> openSession(@RequestBody VotingSessionRequestDTO requestDTO) {
        VotingSessionResponseDTO responseDTO = votingSessionService.openSession(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
