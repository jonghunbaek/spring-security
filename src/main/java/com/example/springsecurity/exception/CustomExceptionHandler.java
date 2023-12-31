package com.example.springsecurity.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureException() {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ExceptionResponse.of("토큰이 유효하지 않습니다."));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJwtException() {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ExceptionResponse.of("올바르지 않은 토큰입니다."));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException() {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ExceptionResponse.of("토큰이 만료되었습니다. 다시 로그인해주세요."));
    }
}
