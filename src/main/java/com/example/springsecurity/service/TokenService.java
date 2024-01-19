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
     * Access Token을 재발행, Refresh Token의 만료시간 및 토큰에 대한 검증은 TokenProvider에서 수행
     * @return 새로운 access token과 기존 refresh token
     */
    public Tokens reissueAccessToken(String accessTokens, String refreshToken) {
        // TODO :: 토큰 재발행할 때 Refresh토큰 db검증 필요한 지 고민해보기.
        String newAccessToken = tokenProvider.reissueAccessToken(accessTokens, refreshToken);

        return Tokens.of(newAccessToken, refreshToken);
    }

    public void deleteRefreshToken(String accessToken, String refreshToken) {
        // TODO :: 리프레쉬 토큰이 변조된 경우 예외조차 터지지 않으므로 실제로 삭제가 이뤄지지 않음.
        //  액세스 토큰에서 memberId를 가져와 id로 리프레쉬 토큰을 가져오고 요청으로 받은 리프레쉬 토큰과 비교 또는 리프레시 토큰 검증
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
