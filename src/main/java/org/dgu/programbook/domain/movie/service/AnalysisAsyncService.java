package org.dgu.programbook.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.entity.MovieUrl;
import org.dgu.programbook.domain.movie.repository.MovieRepository;
import org.dgu.programbook.domain.movie.repository.MovieUrlRepository;
import org.dgu.programbook.domain.movie.util.RestClientUtil;
import org.dgu.programbook.global.error.ErrorCode;
import org.dgu.programbook.global.error.exception.BusinessException;
import org.dgu.programbook.global.error.exception.EntityNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisAsyncService {
    private final MovieRepository movieRepository;
    private final MovieUrlRepository movieUrlRepository;
    private final RestClientUtil restClientUtil;

    //TODO : 로그 정리하기
    @Async
    @Transactional
    public void analyzeAndSave(Movie movie, String fileUrl) {
        try {
            // AI 서버 분석 요청
            AnalysisResponse analysis = restClientUtil.requestAnalysis(fileUrl, movie);

            log.info("AnalysisResponse = prompt2results={}, retrieval2uris={}, thumbnailFolder={}",
                    analysis.getPrompt2results(),
                    analysis.getRetrieval2uris(),
                    analysis.getThumbnail_folder_uri());

            //detached 상태인 movie에 값 저장하는거 방지
            Movie target = movieRepository.findById(movie.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

            String[] promptArray = analysis.getPrompt2results().stream()
                    .filter(item -> item.size() > 1)
                    .map(item -> item.get(1))
                    .toArray(String[]::new);

            String[] uriArray = analysis.getRetrieval2uris().values().stream()
                    .flatMap(List::stream)
                    .toArray(String[]::new);

            log.info("customResults(promptArray) = {}", Arrays.toString(promptArray));
            log.info("retrievalUris(uriArray) = {}", Arrays.toString(uriArray));

            // 분석 결과 저장
            target.updateAnalysisResult(
                    analysis.getThumbnail_folder_uri(),
                    Arrays.copyOf(promptArray, promptArray.length),
                    Arrays.copyOf(uriArray, uriArray.length)
            );

            System.out.println(Arrays.toString(promptArray));
            System.out.println(Arrays.toString(uriArray));

            movieRepository.save(target);
            movieRepository.flush();

            MovieUrl movieUrl = MovieUrl.builder()
                    .movie(target)
                    .movieUrl(fileUrl)
                    .build();

            movieUrlRepository.save(movieUrl);

        } catch (Exception e) {
            // 실패 시 상태 업데이트 등
            movie.updateStatus("FAILED_ANALYSIS");
            movieRepository.save(movie);
        }
    }
}

