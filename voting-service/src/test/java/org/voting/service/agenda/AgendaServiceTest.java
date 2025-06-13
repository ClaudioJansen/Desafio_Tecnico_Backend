package org.voting.service.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.voting.domain.agenda.Agenda;
import org.voting.dto.agenda.AgendaRequestDTO;
import org.voting.dto.agenda.AgendaResponseDTO;
import org.voting.repository.agenda.AgendaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgendaServiceTest {

    private AgendaRepository agendaRepository;
    private AgendaService agendaService;

    @BeforeEach
    void setUp() {
        agendaRepository = mock(AgendaRepository.class);
        agendaService = new AgendaService(agendaRepository);
    }

    @Test
    void shouldCreateAgendaSuccessfully() {
        AgendaRequestDTO requestDTO = AgendaRequestDTO.builder()
                .title("New Agenda")
                .description("Some important discussion")
                .build();

        Agenda savedAgenda = Agenda.builder()
                .id(1L)
                .title("New Agenda")
                .description("Some important discussion")
                .build();

        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        AgendaResponseDTO response = agendaService.createAgenda(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("New Agenda", response.getTitle());
        assertEquals("Some important discussion", response.getDescription());

        ArgumentCaptor<Agenda> agendaCaptor = ArgumentCaptor.forClass(Agenda.class);
        verify(agendaRepository).save(agendaCaptor.capture());

        Agenda captured = agendaCaptor.getValue();
        assertEquals("New Agenda", captured.getTitle());
        assertEquals("Some important discussion", captured.getDescription());
    }
}
