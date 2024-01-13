package com.example.springsecurity.service;

import com.example.springsecurity.dto.SignInRequest;
import com.example.springsecurity.dto.SignUpRequest;
import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repository.MemberRepository;
import com.example.springsecurity.service.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest signUpRequest) {
        Member member = Member.from(signUpRequest, passwordEncoder);

        memberRepository.save(member);
    }

    /**
     * 직접 DB에 접근하고 PasswordEncoder를 이용해 비밀번호 일치 여부 확인
     * @param signInRequest 로그인 요청 정보
     * @return 토큰 생성에 필요한 정보를 반환
     */
    public TokenInfo signIn(SignInRequest signInRequest) {
        Member member = memberRepository.findByEmail(signInRequest.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다. email ::" + signInRequest.getEmail()));

        member.validatePassword(signInRequest.getPassword(), passwordEncoder);

        return TokenInfo.of(member);
    }

    /**
     * DaoAuthenticationProvider를 활용해 로그인 - UserDetailsService
     * @param signInRequest
     * @return
    public Long signIn(SignInRequest signInRequest) {
    Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getEmail(), signInRequest.getPassword());
    Authentication authenticateResponse = authenticationManager.authenticate(authenticationRequest);

    Member member = memberRepository.findByEmail((String) authenticateResponse.getPrincipal()).get();

    return member.getId();
    }
     */
}
