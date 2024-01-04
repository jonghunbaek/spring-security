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

    /**
     * DaoAuthenticationProvider를 활용해 로그인 - UserDetailsService
     * @param signInRequest
     * @return
     */
    public SignInResponse signIn(SignInRequest signInRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getEmail(), signInRequest.getPassword());
        authenticationManager.authenticate(authenticationRequest);

        return SignInResponse.of(
                tokenProvider.createAccessToken(signInRequest.getEmail()),
                tokenProvider.createRefreshToken()
        );
    }

    /**
     * 직접 DB에 접근하고 PasswordEncoder를 이용해 비밀번호 일치 여부 확인
     * @param signInRequest
     * @return
     */
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

    /**
     * Access, Refresh 토큰을 만들고 Refresh Token은 저장 or 수정
     * @param signInRequest
     * @param member
     * @return
     */
    private SignInResponse createTokens(SignInRequest signInRequest, Member member) {
        String accessToken = tokenProvider.createAccessToken(signInRequest.getEmail());
        String refreshToken = tokenProvider.createRefreshToken();

        saveRefreshToken(member, refreshToken);

        return SignInResponse.of(accessToken, refreshToken);
    }

    private boolean isNotMatch(SignInRequest signInRequest, Member member) {
        return !passwordEncoder.matches(signInRequest.getPassword(), member.getPassword());
    }

    private void saveRefreshToken(Member member, String refreshToken) {
        refreshTokenRepository.findById(member.getId())
            .ifPresentOrElse(
                refresh -> refresh.changeNewToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken))
            );
    }
}
