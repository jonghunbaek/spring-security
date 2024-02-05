package com.example.springsecurity.jwt;

import com.example.springsecurity.repository.BlackTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_TYPE = "Bearer ";
    public static final String EXCEPTION_KEY = "exception";

    private final TokenProvider tokenProvider;
    private final BlackTokenRepository blackTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenWithBearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractToken(tokenWithBearer);

        // 블랙 토큰인 지 검증하는 로직 추가

        try {
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            request.setAttribute(EXCEPTION_KEY, e);
            log.error("e :: ", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(String tokenWithBearer) {
        if (StringUtils.hasText(tokenWithBearer)) {
            String authType = tokenWithBearer.substring(0, AUTH_TYPE.length());
            validateAuthType(authType);
            return tokenWithBearer.substring(AUTH_TYPE.length());
        }

        throw new IllegalArgumentException("토큰 값이 존재하지 않습니다.");
    }

    private void validateAuthType(String authType) {
        if (!authType.equalsIgnoreCase(AUTH_TYPE)) {
            throw new IllegalArgumentException("AUTH_TYPE이 일치하지 않습니다. AUTH_TYPE :: " + authType);
        }
    }

    private Authentication getAuthentication(String token) {
        String[] idAndRole = tokenProvider.parseAccessToken(token);

        return new UsernamePasswordAuthenticationToken(
            idAndRole[0],
            "",
            Collections.singletonList(new SimpleGrantedAuthority(idAndRole[1]))
        );
    }
}
