package org.dgu.programbook.domain.movie.dto.response;

import java.time.LocalDateTime;

public class ReadMovieListResponseDto {
    private Long movieId;
    private String title;
    private String thumbnailUrl;

    public ReadMovieListResponseDto(Long movieId, String title, String thumbnailUrl) {
        this.movieId = movieId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getter 생략 또는 @Getter (Lombok) 사용
}
