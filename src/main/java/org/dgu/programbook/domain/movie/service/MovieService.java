package org.dgu.programbook.domain.movie.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.request.CompleteUploadRequestDto;
import org.dgu.programbook.domain.movie.dto.request.CreateMovieRequest;
import org.dgu.programbook.domain.movie.dto.request.UploadFailRequestDto;
import org.dgu.programbook.domain.movie.dto.response.*;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.repository.MovieRepository;
import org.dgu.programbook.domain.movie.repository.MovieUrlRepository;
import org.dgu.programbook.domain.movie.util.RestClientUtil;
import org.dgu.programbook.domain.movie.util.S3Util;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.domain.user.repository.UserRepository;
import org.dgu.programbook.global.error.ErrorCode;
import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {

    private final S3Util s3Util;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RestClientUtil restClientUtil;
    private final MovieUrlRepository movieUrlRepository;
    private final AnalysisAsyncService analysisAsyncService;

    // 업로드 영상 리스트 조회
    public List<ReadMovieListResponse> getMovieList(Long userId) {
        // userId로 사용자가 업로드한 영상들 조회
        List<Movie> movies = movieRepository.findAllByUserId(userId);

        // 엔티티 -> DTO 변환
        return movies.stream()
                .map(movie -> new ReadMovieListResponse(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getThumbnailUrl(),
                        movie.getStatus(),
                        movie.getDirector(),
                        movie.getActor(),
                        movie.getGenre(),
                        movie.getReleaseDate()
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

        // (프롬프트 + 답변) (장면 +uri) 쌍 매핑
        List<PromptResult> promptPairs = mapPromptResults(
                movie.getCustomPrompts(),
                movie.getCustomResults()
        );

        List<RetrievalResult> retrievalResults = mapRetrievalResults(
                movie.getCustomRetrievals(),
                movie.getRetrievalUris()
        );

        // 4. DTO로 변환 및 반환
        return ReadMovieDetailResponse.builder()
                .title(movie.getTitle())
                .director(movie.getDirector())
                .actor(movie.getActor())
                .genre(movie.getGenre())
                .releaseDate(movie.getReleaseDate())
                .thumbnailUrl(movie.getThumbnailUrl())
                .promptResults(promptPairs)
                .retrievalResults(retrievalResults)
                .build();
    }

    //프롬프트-결과 쌍 매핑
    private List<PromptResult> mapPromptResults(String[] prompts, String[] results) {

        if (prompts == null || results == null) return List.of();

        int len = Math.min(prompts.length, results.length);

        return IntStream.range(0, len)
                .mapToObj(i -> PromptResult.builder()
                        .prompt(prompts[i])
                        .result(results[i])
                        .build()
                )
                .toList();
    }

    //장면-uri리스트 쌍 매핑
    private List<RetrievalResult> mapRetrievalResults(String[] retrievals, String[] retrievalUris) {

        if (retrievals == null || retrievalUris == null) return List.of();

        return IntStream.range(0, retrievals.length)
                .filter(i -> i * 3 + 2 < retrievalUris.length)
                .mapToObj(i -> RetrievalResult.builder()
                        .scene(retrievals[i])
                        .uri(List.of(
                                retrievalUris[i * 3],
                                retrievalUris[i * 3 + 1],
                                retrievalUris[i * 3 + 2]
                        ))
                        .build()
                )
                .toList();
    }


    // 멀티 파트 URL 반환
    public CreateUploadResponseDto createUrl(CreateMovieRequest createMovieRequest, Long userId) {

        // 0. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        log.info("createMovieRequest: " + createMovieRequest);

        // 1. 영화 정보 저장
        Movie movie = Movie.movieBuilder()
                .user(user)
                .title(createMovieRequest.title())
                .releaseDate(createMovieRequest.releaseDate())
                .actor(createMovieRequest.actor())
                .director(createMovieRequest.director())
                .genre(createMovieRequest.genre())
                .status("UPLOADING")
                .customPrompts(createMovieRequest.customPrompts().toArray(new String[0]))
                .customRetrievals(createMovieRequest.customRetrievals().toArray(new String[0]))
                .build();

        movieRepository.save(movie);
        log.info("[MovieService] Movie 저장 완료 - movieId: {}", movie.getId());

        // 2. 멀티파트 URL 생성
        CreateUploadResponseDto s3Info = s3Util.initiateMultipartUpload(createMovieRequest.totalParts());
        log.info("[MovieService] S3 Presigned URL 생성 완료 - uploadId: {}, objectKey: {}",
                s3Info.getUploadId(), s3Info.getObjectKey());

        // 3. DTO로 묶어서 반환
        return CreateUploadResponseDto.builder()
                .movieId(movie.getId())
                .uploadId(s3Info.getUploadId())
                .objectKey(s3Info.getObjectKey())
                .presignedParts(s3Info.getPresignedParts())
                .build();
    }

    // 프론트에서 완료 요청 -> S3에서 영상 URL 전달받아 저장 및 프론트 전달
    @Transactional
    public Boolean completeUpload(CompleteUploadRequestDto completeUploadRequestDto, Long userId) {

        log.info("UPLOAD COMPLETE START userId={}, movieId={}", userId, completeUploadRequestDto.movieId());

        // 0. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(completeUploadRequestDto.movieId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        log.info("USER & MOVIE VALIDATED userId={}, movieId={}", userId, movie.getId());

        // 1) S3 멀티파트 업로드 완료
        String fileUrl = s3Util.completeMultipartUpload(
                completeUploadRequestDto.uploadId(),
                completeUploadRequestDto.objectKey(),
                completeUploadRequestDto.presignedParts()
        );

        movie.updateStatus("PENDING");
        movieRepository.save(movie);

        log.info("MOVIE STATUS UPDATED TO PENDING movieId={}", movie.getId());

        // 2) AI 서버에 분석 요청
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                analysisAsyncService.analyzeAndSave(movie, fileUrl);
            }
        });
        //AnalysisResponse analysis = restClientUtil.requestAnalysis(fileUrl, movie);

//        // 3) Movie 엔티티 생성 및 저장
//        movie.updateAnalysisResult(
//                analysis.getThumbnail_folder_uri(),
//                analysis.getFinal_review(),
//                analysis.getFinal_story()
//        );
//
//        movieRepository.save(movie);
//
//        MovieUrl movieUrl = MovieUrl.builder()
//                .movie(movie)
//                .movieUrl(fileUrl)
//                .build();
//
//        movieUrlRepository.save(movieUrl);
        log.info("UPLOAD COMPLETE END (Transaction will commit soon) movieId={}", movie.getId());
        return true;
    }

    public void failUpload(UploadFailRequestDto uploadFailRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(uploadFailRequestDto.movieId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        movie.updateStatus("FAILED_UPLOADING");
        movieRepository.save(movie);
    }
}
