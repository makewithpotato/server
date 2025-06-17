package org.dgu.programbook.domain.programbook.dto.response;

import org.dgu.programbook.domain.programbook.entity.Programbook;

public record ProgrambookResponseDTO(
        Long programbookId,
        String title,
        String thumbnailUrl
) {
    public static ProgrambookResponseDTO from (Programbook programbook) {
        return new ProgrambookResponseDTO(
                programbook.getId(),
                programbook.getTitle(),
                programbook.getThumbnailUrl()
        );
    }
}
