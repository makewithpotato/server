package org.dgu.programbook.domain.movie.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
import org.dgu.programbook.domain.movie.entity.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
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
        Map<String, Object> body = Map.of(
                "s3_folder_path", videoPartUrls,
                "movie_id", movie.getId(),
                "characters_info", movie.getActor(),
                "language_code", "ko-KR",
                "threshold", 30,
                "init", true

        );

        ResponseEntity<AnalysisResponse> responseEntity = restClient.post()
                .uri("")
                .body(body)
                .retrieve()
                .toEntity(AnalysisResponse.class);

        return responseEntity.getBody();
    }
}


