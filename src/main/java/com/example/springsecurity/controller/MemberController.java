package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignInResponse;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.service.MemberService;
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
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> joinMember(@RequestBody SignUpRequest signUpRequest)  {
        memberService.signUp(signUpRequest);

        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public SignInResponse authenticateUser(@RequestBody SignInRequest signInRequest) {
        return memberService.signIn(signInRequest);
    }
}
