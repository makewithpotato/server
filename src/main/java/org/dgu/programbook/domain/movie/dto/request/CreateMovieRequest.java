package org.dgu.programbook.domain.movie.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CreateMovieRequest(
        String title,
        MultipartFile movie
) {
}
