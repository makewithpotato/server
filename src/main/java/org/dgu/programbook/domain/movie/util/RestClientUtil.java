package org.dgu.programbook.domain.movie.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.movie.dto.response.AnalysisResponse;
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

    public AnalysisResponse requestAnalysis(List<String> videoPartUrls) {
        Map<String, Object> body = Map.of("videoUrls", videoPartUrls);

        ResponseEntity<AnalysisResponse> responseEntity = restClient.post()
                .uri("/analyze")
                .body(body)
                .retrieve()
                .toEntity(AnalysisResponse.class);

        return responseEntity.getBody();
    }
}

