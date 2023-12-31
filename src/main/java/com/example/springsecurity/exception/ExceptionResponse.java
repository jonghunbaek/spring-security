package com.example.springsecurity.exception;

public class ExceptionResponse {

    private String message;

    private ExceptionResponse(String message) {
        this.message = message;
    }

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message);
    }
}
