package org.dgu.programbook.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.user.dto.response.UserTokenResponseDTO;
import org.dgu.programbook.domain.user.service.UserService;
import org.dgu.programbook.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;

    // 임시 토큰 발급 API. 추후 로그인 기능이 완성되면 삭제할 예정
    @PostMapping("/token/{userId}")
    public ResponseEntity<SuccessResponse<?>> getTempToken(@PathVariable Long userId) {
        UserTokenResponseDTO userToken = userService.issueTempToken(userId);
        return SuccessResponse.ok(userToken);
    }
}
