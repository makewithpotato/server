package org.dgu.programbook.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dgu.programbook.global.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.dgu.programbook.global.error.ErrorCode.OAUTH_TOKEN_REQUEST_FAILED;

// OAuth 토큰 요청 클래스
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OauthTokenProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public String getOauthToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", URLDecoder.decode(code, StandardCharsets.UTF_8)); // 구글 로그인 인가코드 인코딩 관련 오류 방지 (%2F -> /)
            body.add("client_id", googleClientId);
            body.add("client_secret", googleClientSecret);
            body.add("redirect_uri", googleRedirectUri);
            body.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            log.error("Google OAuth 토큰 요청 오류", e);
            throw new BusinessException(OAUTH_TOKEN_REQUEST_FAILED);
        }
    }


}
