package com.example.springsecurity.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
        return Jwts.builder()
            .signWith(accessSecretKey, Jwts.SIG.HS512)
            .subject(email)
            .issuer(issuer)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(accessExpiration, ChronoUnit.SECONDS)))
            .compact();
    }

    public String createRefreshToken() {
        return Jwts.builder()
                .signWith(refreshSecretKey, Jwts.SIG.HS512)
                .issuer(issuer)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(refreshExpiration, ChronoUnit.SECONDS)))
                .compact();
    }

    public String convertAfterValidate(String token) {
        return Jwts.parser()
            .verifyWith(accessSecretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public void validateRefreshToken(String refreshToken) {
        Jwts.parser()
            .verifyWith(refreshSecretKey)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload()
            .getExpiration();
    }
}
