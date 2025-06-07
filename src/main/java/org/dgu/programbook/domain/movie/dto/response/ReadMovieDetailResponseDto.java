package org.dgu.programbook.domain.movie.dto.response;

import lombok.Builder;

@Builder
public class ReadMovieDetailResponseDto {

    String title;
    String thumbnailUrl;
    String videoUrls;
    String summary;
}
