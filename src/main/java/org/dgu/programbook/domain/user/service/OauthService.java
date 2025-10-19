package org.dgu.programbook.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.user.dto.response.UserTokenResponseDTO;
import org.dgu.programbook.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OauthService {

    private final OauthTokenProvider oauthTokenProvider;
    private final OauthUserResourceProvider oauthUserResourceProvider;

    private final UserService userService;

    @Transactional
    public UserTokenResponseDTO socialLogin(String code){

        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8); //구글 로그인 인가코드 인코딩 관련 오류 방지 (%2F -> /)

        // OAuth 토큰 가져오기
        String oauthToken = oauthTokenProvider.getOauthToken(decodedCode);

        // 사용자 정보 가져오기
        JsonNode userResource = oauthUserResourceProvider.getUserResource(oauthToken);

        // 사용자 정보 처리
        Map<String, String> userInfo = oauthUserResourceProvider.extractUserInfo(userResource);
        String socialId = userInfo.get("socialId");
        String imageUrl = userInfo.get("imageUrl");
        String email = userInfo.get("email");

        // 사용자 저장
        User user = userService.saveUser(socialId, imageUrl, email);

        // JWT 토큰 발급
        // 임시용(리프레쉬 토큰 저장 로직 추가 필요)
        return userService.issueTempToken(user.getId());
    }




}
