package org.voting.controller.agenda;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.voting.dto.agenda.AgendaRequestDTO;
import org.voting.dto.agenda.AgendaResponseDTO;
import org.voting.service.agenda.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaController.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaService agendaService;

    @Autowired
    private ObjectMapper objectMapper;

    private AgendaRequestDTO requestDTO;
    private AgendaResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        requestDTO = AgendaRequestDTO.builder()
                .title("Test Agenda")
                .description("Description for test agenda")
                .build();

        responseDTO = AgendaResponseDTO.builder()
                .id(1L)
                .title("Test Agenda")
                .description("Description for test agenda")
                .build();
    }

    @Test
    void shouldCreateAgendaSuccessfully() throws Exception {
        Mockito.when(agendaService.createAgenda(any(AgendaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Agenda"))
                .andExpect(jsonPath("$.description").value("Description for test agenda"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsMissing() throws Exception {
        AgendaRequestDTO invalidRequest = AgendaRequestDTO.builder()
                .description("Missing title")
                .build();

        mockMvc.perform(post("/api/v1/agendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().is5xxServerError());
    }
}
