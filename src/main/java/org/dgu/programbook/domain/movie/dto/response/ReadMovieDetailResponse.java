package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReadMovieDetailResponse {
    String title;
    String director;
    String actor;
    String genre;
    LocalDate releaseDate;
    String thumbnailUrl;
    String summary;
    String reviews;
}
