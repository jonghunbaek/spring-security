package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tokens {

    private String accessToken;
    private String refreshToken;

    public static Tokens of(String accessToken, String refreshToken) {
        return new Tokens(accessToken, refreshToken);
    }
}
