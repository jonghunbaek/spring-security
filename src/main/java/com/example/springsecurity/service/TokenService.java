package com.example.springsecurity.service;

import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.RefreshTokenRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    /**
     *  Access, Refresh 토큰을 만들고 Refresh Token은 저장 or 수정
     * @param tokenInfo 토큰 생성에 필요한 정보를 담은 DTO
     * @return access, refresh 토큰을 생성해 반환
     */
    public Tokens createTokens(TokenInfo tokenInfo) {
        String accessToken = tokenProvider.createAccessToken(tokenInfo);
        String refreshToken = tokenProvider.createRefreshToken();

        saveRefreshToken(tokenInfo.getMemberId(), refreshToken);

        return Tokens.of(accessToken, refreshToken);
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenRepository.findById(memberId)
            .ifPresentOrElse(
                refresh -> refresh.updateNewToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(memberId, refreshToken))
            );
    }

    /**
     * Access Token을 재발행, Refresh Token의 만료시간 및 토큰에 대한 검증은 TokenProvider에서 수행
     * @param tokens access, refresh token
     * @return 새로운 access token과 기존 refresh token
     */
    public Tokens reissueAccessToken(Tokens tokens) {
        String newAccessToken = tokenProvider.reissueAccessToken(tokens.getAccessToken(), tokens.getRefreshToken());

        return Tokens.of(newAccessToken, tokens.getRefreshToken());
    }
}
