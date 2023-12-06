package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/security")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/sign-in")
    public ResponseEntity<String> authenticateUser(@RequestBody SignInRequest signInRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getEmail(), signInRequest.getPassword());
        Authentication authenticateResponse = authenticationManager.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(authenticateResponse);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getCredentials());
        System.out.println(authentication.getName());
        System.out.println(authentication.isAuthenticated());
        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> joinMember(@RequestBody SignUpRequest signUpRequest)  {
        Member member = Member.builder()
            .name(signUpRequest.getName())
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .build();

        memberRepository.save(member);

        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    }
}
