package org.dgu.programbook.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.dgu.programbook.domain.user.dto.response.UserTokenResponseDTO;
import org.dgu.programbook.domain.user.entity.User;
import org.dgu.programbook.domain.user.repository.UserRepository;
import org.dgu.programbook.global.config.auth.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public String issueNewAccessToken(Long userId) {
        return jwtProvider.getIssueToken(userId, true);
    }

    public String issueNewRefreshToken(Long userId) {
        return jwtProvider.getIssueToken(userId, false);
    }

    public UserTokenResponseDTO issueTempToken(Long userId) {
        String accessToken = issueNewAccessToken(userId);
        String refreshToken = issueNewRefreshToken(userId);
        return UserTokenResponseDTO.of(accessToken, refreshToken);
    }

    // 사용자 정보 저장
    @Transactional
    public User saveUser(String socialId, String imageUrl, String email) {
        // 이미 가입한 유저인지 확인
        User existedUser = getExistedUser(socialId);

        // 신규 유저
        if(existedUser==null) {
            User newUser = User.builder()
                    .socialId(socialId)
                    .imageUrl(imageUrl)
                    .email(email)
                    .build();
            return userRepository.save(newUser);
        }
        return existedUser;
    }

    private User getExistedUser(String socialId) {
        return userRepository.findBySocialId(socialId).orElse(null);
    }
}
