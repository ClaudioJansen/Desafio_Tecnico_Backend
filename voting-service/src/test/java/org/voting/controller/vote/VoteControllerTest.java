package org.voting.controller.vote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.voting.domain.vote.VoteChoice;
import org.voting.dto.result.VoteResultResponseDTO;
import org.voting.dto.vote.VoteRequestDTO;
import org.voting.dto.vote.VoteResponseDTO;
import org.voting.service.vote.VoteService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCastVoteSuccessfully() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO();
        request.setCpf("12345678900");
        request.setSessionId(1L);
        request.setChoice(VoteChoice.YES);

        VoteResponseDTO response = VoteResponseDTO.builder()
                .voteId(1L)
                .sessionId(1L)
                .cpf("12345678900")
                .choice(VoteChoice.YES)
                .build();

        Mockito.when(voteService.castVote(any(VoteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteId").value(1L))
                .andExpect(jsonPath("$.cpf").value("12345678900"))
                .andExpect(jsonPath("$.choice").value("YES"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidVote() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO();
        request.setCpf("12345678900");

        mockMvc.perform(post("/api/v1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturnVoteResult() throws Exception {
        Long agendaId = 1L;

        VoteResultResponseDTO response = VoteResultResponseDTO.builder()
                .agendaId(agendaId)
                .yesVotes(10)
                .noVotes(5)
                .build();

        Mockito.when(voteService.getResultByAgenda(eq(agendaId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/votes/result/{agendaId}", agendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendaId").value(agendaId))
                .andExpect(jsonPath("$.yesVotes").value(10))
                .andExpect(jsonPath("$.noVotes").value(5));
    }
}
