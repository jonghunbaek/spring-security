package com.example.springsecurity.service;

import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.RefreshTokenRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
     * Access Token, Refresh Token 재발행
     * Refresh Token은 jjwt라이브러리를 이용한 만료시간, 변조 여부 1차검증 후
     * DB에 저장된 Refresh Token과 비교해 2차 검증을 한다.
     * @return 새로운 access token과 기존 refresh token
     */
    @Transactional
    public Tokens reissueTokens(String accessTokens, String refreshToken) {
        String newAccessToken = tokenProvider.reissueAccessToken(accessTokens, refreshToken);
        String newRefreshToken = reissueRefreshToken(refreshToken);

        return Tokens.of(newAccessToken, newRefreshToken);
    }

    private String reissueRefreshToken(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("해당 리프레쉬 토큰이 존재하지 않습니다."));

        String newRefreshToken = tokenProvider.createRefreshToken();
        findRefreshToken.updateNewToken(newRefreshToken);
        return newRefreshToken;
    }

    public void deleteRefreshToken(String accessToken, String refreshToken) {
        // TODO :: 리프레쉬 토큰이 변조된 경우 예외조차 터지지 않으므로 실제로 삭제가 이뤄지지 않음.
        //  액세스 토큰에서 memberId를 가져와 id로 리프레쉬 토큰을 가져오고 요청으로 받은 리프레쉬 토큰과 비교 또는 리프레시 토큰 검증
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
