package org.dgu.programbook.domain.programbook.dto.response;

import org.dgu.programbook.domain.programbook.entity.Programbook;

public record ProgramBookPdfResponseDTO(
        String title,
        String thumbnailUrl,
        String pdfUrl
) {
    public static ProgramBookPdfResponseDTO from (Programbook programbook) {
        return new ProgramBookPdfResponseDTO(
                programbook.getTitle(),
                programbook.getThumbnailUrl(),
                programbook.getPdfUrl()
        );
    }
}
