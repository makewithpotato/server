package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadMovieDetailResponse {

    String title;
    String thumbnailUrl;
    String videoUrls;
    String summary;
}
