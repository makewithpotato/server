package org.dgu.programbook.domain.movie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.request.CompleteUploadRequestDto;
import org.dgu.programbook.domain.movie.dto.request.CreateMovieRequest;
import org.dgu.programbook.domain.movie.dto.response.*;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.entity.MovieUrl;
import org.dgu.programbook.domain.movie.repository.MovieRepository;
import org.dgu.programbook.domain.movie.repository.MovieUrlRepository;
import org.dgu.programbook.domain.movie.util.RestClientUtil;
import org.dgu.programbook.domain.movie.util.S3Util;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.domain.user.repository.UserRepository;
import org.dgu.programbook.global.error.ErrorCode;
import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final S3Util s3Util;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RestClientUtil restClientUtil;
    private final MovieUrlRepository movieUrlRepository;

    // 업로드 영상 리스트 조회
    public List<ReadMovieListResponse> getMovieList(Long userId) {
        // userId로 사용자가 업로드한 영상들 조회
        List<Movie> movies = movieRepository.findAllByUserId(userId);

        // 엔티티 -> DTO 변환
        return movies.stream()
                .map(movie -> new ReadMovieListResponse(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }

    // 영상 분석 정보 단일 조회 (레이아웃 구성 요소)
    public ReadMovieDetailResponse getMovieDetail(Long userId, Long movieId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 영화 조회
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // 3. (선택) 영화 소유자 검증
        if (!movie.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN); // 사용자에게 권한 없음
        }

        // 4. DTO로 변환 및 반환
        return ReadMovieDetailResponse.builder()
                .title(movie.getTitle())
                .thumbnailUrl(movie.getThumbnailUrl())
                .summary(movie.getSummary())
                .build();
    }

    // 멀티 파트 URL 반환
    public CreateUploadResponseDto createUrl(CreateMovieRequest createMovieRequest, Long userId) {

        // 0. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. 멀티 파트 URL 생성
        // 2. 프론트 서버에 전달

        return s3Util.initiateMultipartUpload(createMovieRequest.movie());
    }

    // 프론트에서 완료 요청 -> S3에서 영상 URL 전달받아 저장 및 프론트 전달
    @Transactional
    public Boolean completeUpload(CompleteUploadRequestDto completeUploadRequestDto, Long userId) {

        // 0. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1) S3 멀티파트 업로드 완료
        String fileUrl = s3Util.completeMultipartUpload(
                completeUploadRequestDto.uploadId(),
                completeUploadRequestDto.objectKey(),
                completeUploadRequestDto.presignedParts()
        );

        // 2) AI 서버에 분석 요청
        AnalysisResponse analysis = restClientUtil.requestAnalysis(List.of(fileUrl));

        // 3) Movie 엔티티 생성 및 저장
        Movie movie = Movie.movieBuilder()
                .user(user)
                .title(completeUploadRequestDto.title())
                .thumbnailUrl(analysis.getThumbnailUrl())
                .summary(analysis.getSummary())
                .build();

        movieRepository.save(movie);

        MovieUrl movieUrl = MovieUrl.builder()
                .movie(movie)
                .movieUrl(fileUrl)
                .build();

        movieUrlRepository.save(movieUrl);

        return true;
    }
}
