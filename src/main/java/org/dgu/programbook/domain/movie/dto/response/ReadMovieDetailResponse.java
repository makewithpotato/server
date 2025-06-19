package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadMovieDetailResponse {
    String thumbnailUrl;
    String summary;
    String review;
}
