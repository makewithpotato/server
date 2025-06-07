package org.dgu.programbook.domain.movie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.request.CreateMovieRequestDto;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.dto.response.ReadMovieDetailResponseDto;
import org.dgu.programbook.domain.movie.dto.response.ReadMovieListResponseDto;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.repository.MovieRepository;
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

    // 업로드 영상 리스트 조회
    public List<ReadMovieListResponseDto> getMovieList(Long userId) {
        // userId로 사용자가 업로드한 영상들 조회
        List<Movie> movies = movieRepository.findAllByUserId(userId);

        // 엔티티 -> DTO 변환
        return movies.stream()
                .map(movie -> new ReadMovieListResponseDto(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getThumbnailUrl()
                ))
                .collect(Collectors.toList());
    }

    // 영상 S3업로드(분할) + AI Request(RestClient)
    public Boolean uploadMovie(CreateMovieRequestDto createMovieRequestDto) {
        // 1. 영상 S3 업로드
        List<String> videoPartUrls = s3Util.uploadVideoParts(createMovieRequestDto.movie());

        // 2. AI 서버에 분석 요청
        AnalysisResponse analysisResponse = restClientUtil.requestAnalysis(videoPartUrls);

        // 3. Movie 엔티티 생성 및 저장
        Movie movie = Movie.movieBuilder()
                .title(createMovieRequestDto.title())
                .thumbnailUrl(analysisResponse.getThumbnailUrl())
                .summary(analysisResponse.getSummary())
                .build();

        movieRepository.save(movie);
        return true;

    }

    // 영상 분석 정보 단일 조회 (레이아웃 구성 요소)
    public ReadMovieDetailResponseDto getMovieDetail(Long userId, Long movieId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 영화 조회
        Movie movie = movieRepository.findByUserId(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        // 3. (선택) 영화 소유자 검증
        if (!movie.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN); // 사용자에게 권한 없음
        }

        // 4. DTO로 변환 및 반환
        return ReadMovieDetailResponseDto.builder()
                .title(movie.getTitle())
                .thumbnailUrl(movie.getThumbnailUrl())
                .summary(movie.getSummary())
                .build();
    }
}
