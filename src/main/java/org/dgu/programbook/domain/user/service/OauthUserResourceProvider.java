package org.dgu.programbook.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.dgu.programbook.global.error.ErrorCode.OAUTH_USER_RESOURCE_FAILED;

// OAuth 토큰으로 구글 사용자 정보 요청 클래스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OauthUserResourceProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    public JsonNode getUserResource(String oauthToken) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + oauthToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return new ObjectMapper().readTree(response.getBody());
        } catch (Exception e) {
            log.error("Google OAuth 사용자 정보 요청 오류: ", e);
            throw new BusinessException(OAUTH_USER_RESOURCE_FAILED);
        }
    }

    public Map<String, String> extractUserInfo(JsonNode userResource) {
        String socialId = userResource.path("sub").asText();
        String imageUrl = userResource.path("picture").asText();
        String email = userResource.path("email").asText();

        return Map.of(
                "socialId", socialId,
                "imageUrl", imageUrl,
                "email", email
        );
    }
}
