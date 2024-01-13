package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.Tokens;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.TokenService;
import com.example.springsecurity.service.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/security")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    // TODO
    //  남은 기능
    //  1. 로그아웃
    //  2. 로그인 시 토큰 쿠키 보안설정 후 반환
    //  3. OAUTH를 활용한 소셜 로그인
    //  4. 테스트 작성

    @PostMapping("/sign-up")
    public ResponseEntity<String> joinMember(@RequestBody SignUpRequest signUpRequest)  {
        authService.signUp(signUpRequest);

        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public Tokens authenticateUser(@RequestBody SignInRequest signInRequest) {
        TokenInfo tokenInfo = authService.signIn(signInRequest);

        return tokenService.createTokens(tokenInfo);
    }

    @PostMapping("/sign-in/reissue")
    public Tokens reissueTokens(Tokens tokens) {
        return tokenService.reissueAccessToken(tokens);
    }
}
