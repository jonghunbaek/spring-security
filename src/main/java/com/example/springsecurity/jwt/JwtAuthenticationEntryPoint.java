package com.example.springsecurity.jwt;

import com.example.springsecurity.exception.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String EXPIRED_MESSAGE = "토큰이 만료되었습니다.";
    public static final String SIGNATURE_MESSAGE = "토큰이 위조되었습니다.";
    public static final String BLACK_MESSAGE = "해당 토큰은 블랙 리스트 처리된 토큰입니다.";
    public static final String DEFAULT_AUTH_MESSAGE = "토큰이 존재하지 않거나 허가되지 않은 접근입니다.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ExceptionResponse exceptionResponse = createExceptionMessage((Exception) request.getAttribute("exception"));
        setUpResponse(response, exceptionResponse);
    }

    private ExceptionResponse createExceptionMessage(Exception e) {
        if (e instanceof ExpiredJwtException) {
            return ExceptionResponse.of(FORBIDDEN.value(), EXPIRED_MESSAGE);
        }

        if (e instanceof SignatureException) {
            return ExceptionResponse.of(FORBIDDEN.value(), SIGNATURE_MESSAGE);
        }

        if (e instanceof IllegalStateException) {
            return ExceptionResponse.of(FORBIDDEN.value(), BLACK_MESSAGE);
        }

        return ExceptionResponse.of(FORBIDDEN.value(), DEFAULT_AUTH_MESSAGE);
    }

    private void setUpResponse(HttpServletResponse response, ExceptionResponse exceptionResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(exceptionResponse.getErrorCode());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    }
}
