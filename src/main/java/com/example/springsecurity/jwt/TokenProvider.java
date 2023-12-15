package com.example.springsecurity.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TokenProvider {

    private final long expiration;
    private final String issuer;
    private final SecretKey secretKey;

    public TokenProvider(@Value("${secret-key}") String secretKey,
                         @Value("${expiration-hours}") long expiration,
                         @Value("${issuer}") String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public String createToken(String userSpecification) {
        return Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS512)
            .subject(userSpecification)
            .issuer(issuer)
            .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.HOURS)))
            .compact();
    }

    public String validateToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
