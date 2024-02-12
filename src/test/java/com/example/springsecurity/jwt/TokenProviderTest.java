package com.example.springsecurity.jwt;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.service.dto.TokenInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @DisplayName("토큰 생성정보를 인자로 받아 access 토큰을 생성하고 파싱해서 사용자의 아이디를 얻는다.")
    @Test
    void createParseAccessToken() {
        // given
        TokenInfo tokenInfo = TokenInfo.builder()
                .memberId(1L)
                .role(Role.ROLE_USER)
                .build();

        // when
        String accessToken = tokenProvider.createAccessToken(tokenInfo);
        String[] idAndRole = tokenProvider.parseAccessToken(accessToken);

        // then
        Assertions.assertThat(idAndRole[0]).isEqualTo("1");
    }
}