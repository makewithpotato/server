package org.dgu.programbook.domain.movie.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreateMovieRequestDto(
        Long userId,
        String title,
        MultipartFile movie
) {
}
