package org.dgu.programbook.domain.programbook.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ProgramBookSaveRequestDTO(
        String title,
        MultipartFile pdfFile
        ) {
}
