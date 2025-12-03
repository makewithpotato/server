package org.dgu.programbook.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(@Value("${ai.server.url}") String aiUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(10_000);    // 10초
        factory.setReadTimeout(60 * 60 * 1000); // 1시간

        return RestClient.builder()
                .baseUrl(aiUrl)
                .requestFactory(factory)
                .build();
    }
}
