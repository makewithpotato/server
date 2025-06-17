package org.dgu.programbook.domain.programbook.service;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.programbook.dto.response.ProgrambookResponseDTO;
import org.dgu.programbook.domain.programbook.entity.Programbook;
import org.dgu.programbook.domain.programbook.repository.ProgrambookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProgrambookService {

    private final ProgrambookRepository programbookRepository;

    public List<ProgrambookResponseDTO> getProgrambookList(Long userId) {
        List<Programbook> programbookList = programbookRepository.findAllByUserId(userId);

        return programbookList.stream()
                .map(ProgrambookResponseDTO::from)
                .toList();
    }
}
