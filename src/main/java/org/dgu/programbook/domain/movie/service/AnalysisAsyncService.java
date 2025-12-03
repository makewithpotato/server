package org.dgu.programbook.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.dgu.programbook.domain.movie.entity.MovieUrl;
import org.dgu.programbook.domain.movie.repository.MovieRepository;
import org.dgu.programbook.domain.movie.repository.MovieUrlRepository;
import org.dgu.programbook.domain.movie.util.RestClientUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisAsyncService {
    private final MovieRepository movieRepository;
    private final MovieUrlRepository movieUrlRepository;
    private final RestClientUtil restClientUtil;

    @Async
    public void analyzeAndSave(Movie movie, String fileUrl) {
        try {
            // AI 서버 분석 요청
            AnalysisResponse analysis = restClientUtil.requestAnalysis(fileUrl, movie);

            log.info("AnalysisResponse = prompt2results={}, retrieval2uris={}, thumbnailFolder={}",
                    analysis.getPrompt2results(),
                    analysis.getRetrieval2uris(),
                    analysis.getThumbnail_folder_uri());


            // 분석 결과 저장
            movie.updateAnalysisResult(
                    analysis.getThumbnail_folder_uri(),
                    analysis.getPrompt2results().toArray(new String[0]),
                    analysis.getRetrieval2uris().toArray(new String[0])
            );

            movieRepository.save(movie);

            MovieUrl movieUrl = MovieUrl.builder()
                    .movie(movie)
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

