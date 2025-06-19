package org.dgu.programbook.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadMovieListResponse {
    private Long movieId;
    private String title;
    private String thumbnailUrl;
    private String status;
    private String director;
    private String actor;
    private String genre;
    private LocalDate releaseDate;
}
