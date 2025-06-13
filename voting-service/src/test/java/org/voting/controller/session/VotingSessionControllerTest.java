package org.voting.controller.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.voting.dto.session.VotingSessionRequestDTO;
import org.voting.dto.session.VotingSessionResponseDTO;
import org.voting.service.session.VotingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotingSessionService votingSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private VotingSessionRequestDTO requestDTO;
    private VotingSessionResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        requestDTO = VotingSessionRequestDTO.builder()
                .agendaId(1L)
                .durationInMinutes(15)
                .build();

        responseDTO = VotingSessionResponseDTO.builder()
                .sessionId(100L)
                .agendaId(1L)
                .startTime(java.time.LocalDateTime.now())
                .endTime(java.time.LocalDateTime.now().plusMinutes(15))
                .build();
    }

    @Test
    void shouldOpenVotingSessionSuccessfully() throws Exception {
        Mockito.when(votingSessionService.openSession(any(VotingSessionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value(100L))
                .andExpect(jsonPath("$.agendaId").value(1L));
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"agendaId\":null,\"durationInMinutes\":null}"))
                .andExpect(status().is5xxServerError());
    }
}
