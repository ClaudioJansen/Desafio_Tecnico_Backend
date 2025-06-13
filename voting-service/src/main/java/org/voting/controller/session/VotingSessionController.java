package org.voting.controller.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Voting Session", description = "Endpoints for managing voting sessions")
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @Operation(
            summary = "Open a voting session",
            description = "Opens a voting session for a given agenda.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Session successfully opened"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or session already exists")
            }
    )
    @PostMapping
    public ResponseEntity<VotingSessionResponseDTO> openSession(@RequestBody VotingSessionRequestDTO requestDTO) {
        VotingSessionResponseDTO responseDTO = votingSessionService.openSession(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
