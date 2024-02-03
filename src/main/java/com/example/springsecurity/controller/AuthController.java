package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.TokenService;
import com.example.springsecurity.service.dto.TokenInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/security")
@RequiredArgsConstructor
@RestController
public class AuthController {

    public static final String[] TOKEN_TYPE = {"access", "refresh"};
    public static final int COOKIE_MAX_AGE = 60 * 60 * 24;
    private final AuthService authService;
    private final TokenService tokenService;

    // TODO
    //  남은 기능
    //  1. 로그아웃
    //  2. OAUTH를 활용한 소셜 로그인
    //  3. 테스트 작성

    @PostMapping("/sign-up")
    public ResponseEntity<String> joinMember(@RequestBody SignUpRequest signUpRequest)  {
        authService.signUp(signUpRequest);

        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public void authenticateUser(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        TokenInfo tokenInfo = authService.signIn(signInRequest);
        Tokens tokens = tokenService.createTokens(tokenInfo);

        setUpTokensToCookie(tokens, response);
    }

    @PostMapping("/sign-in/reissue")
    public void reissueTokens(
            @CookieValue(name = "refresh") String refreshToken,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
            HttpServletResponse response) {

        Tokens newTokens = tokenService.reissueTokens(accessToken, refreshToken);

        setUpTokensToCookie(newTokens, response);
    }
    
    @PostMapping("/logout")
    public void logout(
            @CookieValue(name = "refresh") String refreshToken,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
            HttpServletResponse response) {

        tokenService.deleteRefreshToken(accessToken, refreshToken);
        clearTokensFromCookie(response);
    }

    private void clearTokensFromCookie(HttpServletResponse response) {
        ResponseCookie accessCookie = createCookie(TOKEN_TYPE[0], "", false, 0);
        ResponseCookie refreshCookie = createCookie(TOKEN_TYPE[1], "", false, 0);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void setUpTokensToCookie(Tokens tokens, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = createCookie(TOKEN_TYPE[0], tokens.getAccessToken(), false, COOKIE_MAX_AGE);
        ResponseCookie refreshTokenCookie = createCookie(TOKEN_TYPE[1], tokens.getRefreshToken(), true, COOKIE_MAX_AGE);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private ResponseCookie createCookie(String tokenType, String token, boolean isHttpOnly, long maxAge) {
        return ResponseCookie.from(tokenType, token)
            .httpOnly(isHttpOnly)
            .secure(true)
            .path("/")
            .maxAge(maxAge)
            .sameSite("None")
            .build();
    }
}
