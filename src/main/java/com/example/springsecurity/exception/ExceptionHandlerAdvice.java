package com.example.springsecurity.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(SQLException.class)
    public String message() {
        return "sql 예외 발생";
    }
}
