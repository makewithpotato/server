package org.dgu.programbook.domain.movie.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientUtil {

    private RestClient restClient;

    @Value("${ai.server.url}")
    private String aiUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(aiUrl)
                .build();
    }

    public AnalysisResponse requestAnalysis(String videoPartUrls, Movie movie) {
        log.info("AI 분석 요청 시작");

        Map<String, Object> body = Map.of(
                "s3_video_uri", videoPartUrls,
                "characters_info", movie.getActor(),
                "movie_id", movie.getId(),
                "segment_duration", 600,
                "init", false,
                "language_code", "ko-KR",
                "threshold", 30,
                "custom_prompts", Arrays.asList(movie.getCustomPrompts()),
                "custom_retrievals", Arrays.asList(movie.getCustomRetrievals())
        );

        log.info("AI Request Body = {}", body);

        ResponseEntity<AnalysisResponse> responseEntity = restClient.post()
                .uri("")
                .body(body)
                .retrieve()
                .toEntity(AnalysisResponse.class);

        return responseEntity.getBody();
    }

}


