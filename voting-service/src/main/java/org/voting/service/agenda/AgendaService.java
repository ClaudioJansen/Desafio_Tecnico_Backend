package org.voting.service.agenda;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.agenda.Agenda;
import org.voting.dto.agenda.AgendaRequestDTO;
import org.voting.dto.agenda.AgendaResponseDTO;
import org.voting.repository.agenda.AgendaRepository;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaResponseDTO createAgenda(AgendaRequestDTO dto) {
        Agenda agenda = Agenda.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        Agenda saved = agendaRepository.save(agenda);

        return new AgendaResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }
}
