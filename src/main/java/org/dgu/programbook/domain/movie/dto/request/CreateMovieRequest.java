package org.dgu.programbook.domain.movie.dto.request;

import java.time.LocalDate;
import java.util.List;

public record CreateMovieRequest(
        String title,
        String director,
        String genre,
        LocalDate releaseDate,
        String actor,
        Long totalParts,
        List<String> customPrompts,
        List<String> customRetrievals
) {
}
