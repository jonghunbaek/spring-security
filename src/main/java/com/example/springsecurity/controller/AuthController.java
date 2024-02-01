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

    private final AuthService authService;
    private final TokenService tokenService;

    // TODO
    //  남은 기능
    //  1. 로그아웃
    //  2. OAUTH를 활용한 소셜 로그인
    //  3. 테스트 작성
    //  4. RTR기법 적용

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
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken) {
        // TODO
        //  Access 토큰 블랙리스트 처리할 방법 찾기
        tokenService.deleteRefreshToken(accessToken, refreshToken);
    }

    private void setUpTokensToCookie(Tokens tokens, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = createCookie("access", tokens.getAccessToken(), false);
        ResponseCookie refreshTokenCookie = createCookie("refresh", tokens.getRefreshToken(), true);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private ResponseCookie createCookie(String tokenType, String token, boolean isHttpOnly) {
        return ResponseCookie.from(tokenType, token)
            .httpOnly(isHttpOnly)
            .secure(true)
            .path("/")
            .maxAge(60 * 60 * 24)
            .sameSite("None")
            .build();
    }
}
