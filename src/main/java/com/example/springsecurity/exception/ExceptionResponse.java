package com.example.springsecurity.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ExceptionResponse implements Serializable {

    private int errorCode;
    private String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    private ExceptionResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message);
    }

    public static ExceptionResponse of(int errorCode, String message) {
        return new ExceptionResponse(errorCode, message);
    }
}
