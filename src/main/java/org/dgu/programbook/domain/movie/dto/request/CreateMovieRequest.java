package org.dgu.programbook.domain.movie.dto.request;

public record CreateMovieRequest(
        String title,
        Long totalParts
) {
}
