package com.example.springsecurity.service;

import com.example.springsecurity.dto.SignInResponse;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.springsecurity.entity.Role.*;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public void signUp(SignUpRequest signUpRequest) {
        Member member = Member.builder()
            .name(signUpRequest.getName())
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .role(valueOf(signUpRequest.getRole()))
            .build();

        memberRepository.save(member);
    }

    // 추후 UserDetailsService를 사용하지 않는 경우도 작성해보자.
    public SignInResponse signIn(SignInRequest signInRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getEmail(), signInRequest.getPassword());
        authenticationManager.authenticate(authenticationRequest);

        return SignInResponse.of(
                tokenProvider.createAccessToken(signInRequest.getEmail()),
                tokenProvider.createRefreshToken()
        );
    }
}
