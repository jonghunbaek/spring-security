package com.example.springsecurity.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(SQLException.class)
    public String message(SQLException e) {
        log.error("e ::", e);
        return "sql 예외 발생";
    }
}
