package com.example.springsecurity.service;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    /**
     * Access, Refresh 토큰을 만들고 Refresh Token은 저장 or 수정
     * @param signInRequest
     * @param memberId
     * @return
     */
    public Tokens createTokens(SignInRequest signInRequest, Long memberId) {
        // TODO : Role 어떻게 꺼내올 지 고민 필요
        String subject = createSubject(signInRequest.getEmail(), Role.ROLE_USER);
        String accessToken = tokenProvider.createAccessToken(subject);
        String refreshToken = tokenProvider.createRefreshToken();

        saveRefreshToken(memberId, refreshToken);

        return Tokens.of(accessToken, refreshToken);
    }

    private String createSubject(String email, Role role) {
        return email + ":" + role.toString();
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.findById(memberId)
            .ifPresentOrElse(
                refresh -> refresh.changeNewToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(memberId, refreshToken))
            );
    }

    /**
     * Access Token을 재발행, Refresh Token의 만료시간 및 토큰에 대한 검증은 TokenProvider에서 수행
     * @param tokens
     * @return
     */
    public Tokens reissueAccessToken(Tokens tokens) {
        String newAccessToken = tokenProvider.reissueAccessToken(tokens.getAccessToken(), tokens.getRefreshToken());

        return Tokens.of(newAccessToken, tokens.getRefreshToken());
    }
}
