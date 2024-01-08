package com.example.study;

import com.example.springsecurity.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JwtParserBuilderTest {

    TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider(
                "NiOeyFbN1Gqo10bPgUyTFsRMkJpGLXSvGP04eFqj5B30r5TcrtlSXfQ7TndvYjNvfkEKLqILn0j1SmKODO1Yw3JpBBgI3nVPEahqxeY8qbPSFGyzyEVxnl4AQcrnVneI",
                "NiOeyFbN1Gqo10bPgUyTFsRMkJpGLXSvGP04eFqj5B30r5TcrtlSXfQ7TndvYjNvfkEKLqILn0j1SmKODO1Yw3JpBBgI3nVPEahqxeY8qbPSFGyzyEVxnl4AQcrnVneI",
                5,
                5,
                "jonghun"
        );
    }

    @DisplayName("JwtParserBuilder.verify()가 만료시간을 검증하는 지 테스트한다.")
    @Test
    void checkExpire() throws InterruptedException {
        String refreshToken = tokenProvider.createRefreshToken();

        Thread.sleep(5000);

        assertThatThrownBy(() -> tokenProvider.validateRefreshToken(refreshToken))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("JwtParserBuilder.verify()가 토큰 유효 상태를 검증하는 지 테스트한다.")
    @Test
    void checkValidation() {
        String refreshToken = tokenProvider.createRefreshToken() + "123";

        assertThatThrownBy(() -> tokenProvider.validateRefreshToken(refreshToken))
            .isInstanceOf(SignatureException.class);
    }
}
