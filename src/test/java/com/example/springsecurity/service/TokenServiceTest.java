package com.example.springsecurity.service;

import com.example.springsecurity.entity.Member;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.repository.BlackTokenRepository;
import com.example.springsecurity.repository.MemberRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import org.assertj.core.api.Assertions;
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
    BlackTokenRepository blackTokenRepository;

    @DisplayName("Redis에 저장된 black token이 만료 시간 후에 삭제가 되었는 지 확인한다.")
    @Test
    void checkBlackTokenExpiration() throws InterruptedException {
        // given
        Member saveMember = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .password("zxcv1234")
                .name("hong")
                .role(ROLE_USER)
                .build());

        String accessToken = tokenProvider.createAccessToken(TokenInfo.of(saveMember));

        // when
        tokenService.logoutTokens(accessToken);
//        Thread.sleep(10000);

        // then
        assertThatThrownBy(() -> blackTokenRepository.findById(accessToken)
                        .orElseThrow(() -> new IllegalArgumentException("해당 블랙 토큰은 존재하지 않습니다.")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}