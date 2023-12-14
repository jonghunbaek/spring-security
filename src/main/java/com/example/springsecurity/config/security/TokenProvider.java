package com.example.springsecurity.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TokenProvider {

    private final String secretKey;
    private final long expiration;
    private final String issuer;

    public TokenProvider(@Value("${secret-key}") String secretKey,
                         @Value("${expiration-hours}") long expiration,
                         @Value("${issuer}") String issuer) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public String createToken(String userSpecification) {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
            .setSubject(userSpecification)
            .setIssuer(issuer)
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(expiration, ChronoUnit.HOURS)))
            .compact();
    }

    public String validateToken(String token) {
        return null;
    }
}
