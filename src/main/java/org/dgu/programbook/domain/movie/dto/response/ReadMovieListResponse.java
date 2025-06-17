package org.dgu.programbook.domain.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadMovieListResponse {
    private Long movieId;
    private String title;
    private String thumbnailUrl;

}
