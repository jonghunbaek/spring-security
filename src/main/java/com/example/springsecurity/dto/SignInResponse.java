package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignInResponse {

    private String accessToken;
    private String refreshToken;

    public static SignInResponse of(String accessToken, String refreshToken) {
        return new SignInResponse(accessToken, refreshToken);
    }
}
