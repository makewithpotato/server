package org.dgu.programbook.domain.programbook.service;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.programbook.dto.response.ProgramBookPdfResponseDTO;
import org.dgu.programbook.domain.programbook.dto.response.ProgrambookResponseDTO;
import org.dgu.programbook.domain.programbook.entity.Programbook;
import org.dgu.programbook.domain.programbook.repository.ProgrambookRepository;
import org.dgu.programbook.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.dgu.programbook.global.error.ErrorCode.PROGRAMBOOK_NOT_FOUND;

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

    public ProgramBookPdfResponseDTO getProgrambook(Long programbookId) {
        Programbook programbook = programbookRepository.findById(programbookId)
                .orElseThrow(() -> new EntityNotFoundException(PROGRAMBOOK_NOT_FOUND));
        return ProgramBookPdfResponseDTO.from(programbook);
    }
}
