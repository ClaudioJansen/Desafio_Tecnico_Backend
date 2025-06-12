package org.voting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.voting.domain.entity.Agenda;
import org.voting.dto.AgendaRequestDTO;
import org.voting.dto.AgendaResponseDTO;
import org.voting.repository.AgendaRepository;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaResponseDTO createAgenda(AgendaRequestDTO dto) {
        Agenda agenda = Agenda.builder()
                .title(dto.title())
                .description(dto.description())
                .build();

        Agenda saved = agendaRepository.save(agenda);

        return new AgendaResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }
}
