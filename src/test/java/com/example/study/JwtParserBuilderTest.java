package com.example.study;

import com.example.springsecurity.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @DisplayName("JwtParserBuilder.verify()가 만료시간도 확인하는 지 테스트한다.")
    @Test
    void checkExpire() throws InterruptedException {
        String refreshToken = tokenProvider.createRefreshToken();

        Thread.sleep(5000);

        try {
            tokenProvider.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료" + e);
        }
    }
}
