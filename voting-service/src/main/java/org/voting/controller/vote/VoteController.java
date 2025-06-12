package org.voting.controller.vote;

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
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponseDTO> vote(@RequestBody VoteRequestDTO request) {
        VoteResponseDTO response = voteService.castVote(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/result/{agendaId}")
    public ResponseEntity<VoteResultResponseDTO> getVoteResult(@PathVariable Long agendaId) {
        VoteResultResponseDTO result = voteService.getResultByAgenda(agendaId);
        return ResponseEntity.ok(result);
    }
}
