package com.example.springsecurity.service;

import com.example.springsecurity.dto.SignInResponse;
import com.example.springsecurity.entity.RefreshToken;
import com.example.springsecurity.jwt.TokenProvider;
import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repository.MemberRepository;
import com.example.springsecurity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.springsecurity.entity.Role.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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

    public SignInResponse signIn(SignInRequest signInRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getEmail(), signInRequest.getPassword());
        authenticationManager.authenticate(authenticationRequest);

        return SignInResponse.of(
                tokenProvider.createAccessToken(signInRequest.getEmail()),
                tokenProvider.createRefreshToken()
        );
    }

    public SignInResponse signInV2(SignInRequest signInRequest) {
        Member member = memberRepository.findByEmail(signInRequest.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다. email ::" + signInRequest.getEmail()));

        validatePw(signInRequest, member);

        return createTokens(signInRequest, member);
    }

    private void validatePw(SignInRequest signInRequest, Member member) {
        if (isNotMatch(signInRequest, member)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private boolean isNotMatch(SignInRequest signInRequest, Member member) {
        return !passwordEncoder.matches(signInRequest.getPassword(), member.getPassword());
    }

    private SignInResponse createTokens(SignInRequest signInRequest, Member member) {
        String accessToken = tokenProvider.createAccessToken(signInRequest.getEmail());
        String refreshToken = tokenProvider.createRefreshToken();

        refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken));

        return SignInResponse.of(accessToken, refreshToken);
    }
}
