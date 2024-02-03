package com.example.springsecurity.service;

import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.RefreshTokenRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

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
     * Refresh Token은 jjwt라이브러리를 이용해 파싱하면서 만료시간, 변조 여부 1차검증 후
     * DB에 저장된 Refresh Token과 비교해 2차 검증을 한다.
     * @return 새로운 access token과 기존 refresh token
     */
    @Transactional
    public Tokens reissueTokens(String accessTokens, String refreshToken) {
        String newAccessToken = tokenProvider.reissueAccessToken(accessTokens);
        String newRefreshToken = reissueRefreshToken(refreshToken);

        return Tokens.of(newAccessToken, newRefreshToken);
    }

    private String reissueRefreshToken(String refreshToken) {
        tokenProvider.validateRefreshToken(refreshToken);
        RefreshToken findRefreshToken = findRefreshToken(refreshToken, "해당 리프레쉬 토큰이 존재하지 않습니다.");

        String newRefreshToken = tokenProvider.createRefreshToken();
        findRefreshToken.updateNewToken(newRefreshToken);
        return newRefreshToken;
    }

    public void deleteRefreshToken(String accessToken, String refreshToken) {
        // TODO
        //  액세스 토큰 레디스 활용해 블랙리스트 처리, 만료시간 가져오기
        //  리프레쉬 토큰이 db에 저장된 것과 다르면 리프레쉬 토큰이 변조 된 것으로 가정해야 하나?
        Date expiration = tokenProvider.getExpiration(accessToken);

        // expiration만큼 redis에서 액세스 토큰 블랙리스트 처리

        tokenProvider.validateRefreshToken(refreshToken);
        RefreshToken findRefreshToken = findRefreshToken(refreshToken,"해당 리프레쉬 토큰이 존재하지 않아 로그아웃을 완료할 수 없습니다.");
        refreshTokenRepository.deleteByToken(findRefreshToken.getToken());
    }

    private RefreshToken findRefreshToken(String refreshToken, String message) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }
}
