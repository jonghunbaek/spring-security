package com.example.springsecurity.jwt;

import com.example.springsecurity.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_TYPE = "Bearer ";

    private final TokenProvider tokenProvider;
    private final CustomUserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> tokenWithBearer = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (tokenWithBearer.isPresent()) {
            log.info("bearer token :: {}", tokenWithBearer);
            String token = extractToken(tokenWithBearer.get());
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
            log.info("is authenticated? :: {}", authentication1.isAuthenticated());
            log.info("email :: {}", authentication1.getPrincipal().toString());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(String tokenWithBearer) {
        String authType = tokenWithBearer.substring(0, AUTH_TYPE.length());
        validateAuthType(authType);

        return tokenWithBearer.substring(AUTH_TYPE.length());
    }

    private void validateAuthType(String authType) {
        if (!authType.equalsIgnoreCase(AUTH_TYPE)) {
            throw new IllegalArgumentException("AUTH_TYPE이 일치하지 않습니다. AUTH_TYPE :: " + authType);
        }
    }

    private Authentication getAuthentication(String token) {
        String subject = parseToSubject(token);
        UserDetails userDetails = userDetailService.loadUserByUsername(subject);

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "", userDetails.getAuthorities());
    }

    private String parseToSubject(String token) {
        return Optional.ofNullable(token)
            .filter(t -> t.length() >= 10)
            .map(tokenProvider::convertAfterValidate)
            .orElse(null);
    }
}
