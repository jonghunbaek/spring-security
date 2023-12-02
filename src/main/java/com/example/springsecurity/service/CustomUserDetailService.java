package com.example.springsecurity.service;

import com.example.springsecurity.entity.Member;
import com.example.springsecurity.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username)
            .orElseThrow(() -> new IllegalArgumentException("이름이 일치하는 사용자가 없습니다."));

        Set<SimpleGrantedAuthority> authorities = member.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

        // withDefaultPasswordEncoder는 학습용으로만 사용
        UserDetails userDetails = User.withDefaultPasswordEncoder()
            .username(username)
            .password(member.getPassword())
            .build();
        return new User(username, member.getPassword(), authorities);
    }
}
