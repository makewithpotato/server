package org.dgu.programbook.domain.movie.controller;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.request.CreateMovieRequestDto;
import org.dgu.programbook.domain.movie.service.MovieService;
import org.dgu.programbook.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    // 업로드 영상 리스트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<SuccessResponse<?>> getMovieList(
            @PathVariable Long userId) {

        return SuccessResponse.ok(movieService.getMovieList(userId));
    }

    // 영상 S3업로드(분할) + AI Request
    @PostMapping("/{userId}")
    public ResponseEntity<SuccessResponse<?>> uploadMovie(
            @RequestBody CreateMovieRequestDto createMovieRequestDto) {

        return SuccessResponse.ok(movieService.uploadMovie(createMovieRequestDto));
    }

    // 영상 분석 정보 단일 조회 (레이아웃 구성 요소)
    @GetMapping("/{userId}/{movieId}")
    public ResponseEntity<SuccessResponse<?>> getMovieDetail(
            @PathVariable Long userId,
            @PathVariable Long movieId) {

        return SuccessResponse.ok(movieService.getMovieDetail(userId, movieId));
    }
}
