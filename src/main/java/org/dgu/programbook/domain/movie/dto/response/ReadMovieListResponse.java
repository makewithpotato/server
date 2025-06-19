package org.dgu.programbook.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dgu.programbook.domain.movie.entity.Status;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadMovieListResponse {
    private Long movieId;
    private String title;
    private String thumbnailUrl;
    private Status status;
    private String director;
    private String actor;
    private String genre;
    private LocalDate releaseDate;
}
