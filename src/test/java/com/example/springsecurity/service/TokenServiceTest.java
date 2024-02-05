package com.example.springsecurity.service;

import com.example.springsecurity.entity.BlackToken;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.BlackTokenRepository;
import com.example.springsecurity.repository.MemberRepository;
import com.example.springsecurity.repository.RefreshTokenRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.springsecurity.entity.Role.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    TokenService tokenService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    BlackTokenRepository blackTokenRepository;

    @DisplayName("리프레쉬 토큰이 DB에서 삭제되고, 액세스 토큰이 레디스에 black 토큰으로 저장되는 지 확인한다.")
    @Test
    void blockTokensTest() {
        // given
        Member saveMember = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .password("zxcv1234")
                .name("hong")
                .role(ROLE_USER)
                .build());

        String accessToken = tokenProvider.createAccessToken(TokenInfo.of(saveMember));
        String refreshToken = tokenProvider.createRefreshToken();

        refreshTokenRepository.save(new RefreshToken(saveMember.getId(), refreshToken));

        // when
        tokenService.blockTokens(accessToken);

        // then
        BlackToken blackToken = blackTokenRepository.findById(accessToken)
            .orElseThrow(() -> new IllegalArgumentException("해당 블랙 토큰은 존재하지 않습니다."));

        assertThat(blackToken.getToken()).isEqualTo(accessToken);

        assertThatThrownBy(() -> refreshTokenRepository.findById(saveMember.getId())
            .orElseThrow(() -> new IllegalArgumentException("리프레쉬 토큰이 존재하지 않습니다.")))
            .isInstanceOf(IllegalArgumentException.class);
    }
}