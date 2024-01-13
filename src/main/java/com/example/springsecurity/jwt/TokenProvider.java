package com.example.springsecurity.jwt;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.service.dto.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

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

    public String createAccessToken(TokenInfo tokenInfo) {
        String subject = createSubject(tokenInfo.getEmail(), tokenInfo.getRole());
        return createToken(subject, accessSecretKey, accessExpiration);
    }

    private String createSubject(String email, Role role) {
        return email + ":" + role.toString();
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
        JwtParser jwtParser = createJwtParser(accessSecretKey);

        return parseToken(token, jwtParser);
    }

    private JwtParser createJwtParser(SecretKey secretKey) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build();
    }

    private static String parseToken(String token, JwtParser jwtParser) {
        return jwtParser.parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String reissueAccessToken(String accessToken, String refreshToken) {
        validateRefreshToken(refreshToken);

        String email = decodeJwtPayload(accessToken);
        return createToken(email, accessSecretKey, accessExpiration);
    }

    private void validateRefreshToken(String token) {
        JwtParser jwtParser = createJwtParser(refreshSecretKey);

        parseToken(token, jwtParser);
    }

    private String decodeJwtPayload(String oldAccessToken) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]), StandardCharsets.UTF_8),
                Map.class
            ).get("sub").toString();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
