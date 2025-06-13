package org.voting.controller.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.service.vote.VoteService;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
@Tag(name = "Vote", description = "Endpoints for casting votes and retrieving results")
public class VoteController {

    private final VoteService voteService;

    @Operation(
            summary = "Cast a vote",
            description = "Allows a user to vote on a session using CPF and vote choice.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vote successfully recorded"),
                    @ApiResponse(responseCode = "400", description = "Vote not allowed or invalid data")
            }
    )
    @PostMapping
    public ResponseEntity<VoteResponseDTO> vote(@Valid @RequestBody VoteRequestDTO request) {
        VoteResponseDTO response = voteService.castVote(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get vote result by agenda",
            description = "Returns the total YES and NO votes for a given agenda ID. Only available after session ends.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vote result retrieved"),
                    @ApiResponse(responseCode = "500", description = "Agenda not found or session still open")
            }
    )
    @GetMapping("/result/{agendaId}")
    public ResponseEntity<VoteResultResponseDTO> getVoteResult(@PathVariable @Valid Long agendaId) {
        VoteResultResponseDTO result = voteService.getResultByAgenda(agendaId);
        return ResponseEntity.ok(result);
    }
}
