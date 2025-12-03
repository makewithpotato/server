package org.dgu.programbook.domain.movie.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientUtil {

    private final RestClient restClient;

    public AnalysisResponse requestAnalysis(String videoPartUrls, Movie movie) {
        log.info("AI 분석 요청 시작");

        Map<String, Object> body = Map.of("s3_video_uri", videoPartUrls, "characters_info", movie.getActor(), "movie_id", movie.getId(), "segment_duration", 600, "init", false, "language_code", "ko-KR", "threshold", 30, "custom_prompts", Arrays.asList(movie.getCustomPrompts()), "custom_retrievals", Arrays.asList(movie.getCustomRetrievals()));

        log.info("AI Request Body = {}", body);

        try {
            ResponseEntity<AnalysisResponse> responseEntity = restClient.post().uri("").body(body).retrieve().toEntity(AnalysisResponse.class);

            log.info("AI Response Body = {}", responseEntity.getBody());
            log.info("AI 분석 요청 끝");

            return responseEntity.getBody();

        } catch (Exception e) {
            log.error("AI 분석 요청 실패 (RestClient 예외 발생)", e);
            throw e;
        }
    }

}


