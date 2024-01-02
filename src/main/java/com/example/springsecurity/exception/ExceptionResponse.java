package com.example.springsecurity.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ExceptionResponse implements Serializable {

    private String message;

    private ExceptionResponse(String message) {
        this.message = message;
    }

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message);
    }
}
