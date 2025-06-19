package org.dgu.programbook.domain.movie.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateMovieRequest(
        String title,
        String director,
        String genre,
        LocalDate releaseDate,
        String actor,
        Long totalParts
) {
}
