package org.dgu.programbook.domain.movie.controller;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.request.CreateMovieRequest;
import org.dgu.programbook.domain.movie.dto.request.CompleteUploadRequestDto;
import org.dgu.programbook.domain.movie.dto.request.UploadFailRequestDto;
import org.dgu.programbook.domain.movie.service.MovieService;
import org.dgu.programbook.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {

    private final MovieService movieService;

    // 업로드 영상 리스트 조회
    @GetMapping("")
    public ResponseEntity<SuccessResponse<?>> getMovieList(
           @AuthenticationPrincipal Long userId) {

        return SuccessResponse.ok(movieService.getMovieList(userId));
    }

    // 영상 분석 정보 단일 조회 (레이아웃 구성 요소)
    @GetMapping("/{movieId}")
    public ResponseEntity<SuccessResponse<?>> getMovieDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long movieId) {

        return SuccessResponse.ok(movieService.getMovieDetail(userId, movieId));
    }

    // 멀티 파트 URL 만들기
    @PostMapping("/url")
    public ResponseEntity<SuccessResponse<?>> uploadMovie(
            @RequestBody CreateMovieRequest createMovieRequest,
            @AuthenticationPrincipal Long userId) {
        return SuccessResponse.ok(movieService.createUrl(createMovieRequest, userId));
    }

    // 영상 업로드 완료 및 영상 정보 저장
    @PostMapping("/complete")
    public ResponseEntity<SuccessResponse<?>> completeUploadMovie(
            @RequestBody CompleteUploadRequestDto completeUploadRequestDto,
            @AuthenticationPrincipal Long userId) {
        return SuccessResponse.ok(movieService.completeUpload(completeUploadRequestDto, userId));
    }

    @PostMapping("/fail")
    public ResponseEntity<SuccessResponse<?>> completeUploadMovie(
            @RequestBody UploadFailRequestDto uploadFailRequestDto,
            @AuthenticationPrincipal Long userId) {
        movieService.failUpload(uploadFailRequestDto,userId);
        return SuccessResponse.ok(null);
    }
}
