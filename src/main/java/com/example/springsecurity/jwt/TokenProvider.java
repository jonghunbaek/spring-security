package com.example.springsecurity.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenProvider {

    private final long accessExpiration;
    private final long refreshExpiration;
    private final String issuer;
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;

    public TokenProvider(@Value("${access-secret-key}") String accessSecretKey,
                         @Value("${refresh-secret-key}") String refreshSecretKey,
                         @Value("${access-expiration-hours}") long accessExpiration,
                         @Value("${refresh-expiration-hours}") long refreshExpiration,
                         @Value("${issuer}") String issuer) {
        this.accessSecretKey = Keys.hmacShaKeyFor(accessSecretKey.getBytes());
        this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretKey.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    public String createAccessToken(String email) {
        return createToken(email, accessSecretKey, accessExpiration);
    }

    public String createRefreshToken() {
        return createToken("", refreshSecretKey, refreshExpiration);
    }

    private String createToken(String subject, SecretKey secretKey, long expiration) {
        return Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS512)
            .subject(subject)
            .issuer(issuer)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS)))
            .compact();
    }

    public String parseAccessToken(String token) {
        JwtParser jwtParser = createJwtParserBy(accessSecretKey);

        return parseToken(token, jwtParser);
    }

    public void validateRefreshToken(String token) {
        JwtParser jwtParser = createJwtParserBy(refreshSecretKey);

        parseToken(token, jwtParser);
    }

    private JwtParser createJwtParserBy(SecretKey secretKey) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build();
    }

    private static String parseToken(String token, JwtParser jwtParser) {
        return jwtParser.parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
