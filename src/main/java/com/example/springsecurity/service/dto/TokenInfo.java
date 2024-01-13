package com.example.springsecurity.service.dto;

import com.example.springsecurity.entity.Member;
import com.example.springsecurity.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfo {

    private Long memberId;
    private String email;
    private Role role;

    @Builder
    private TokenInfo(Long memberId, String email, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.role = role;
    }

    public static TokenInfo of(Member member) {
        return TokenInfo.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .role(member.getRole())
            .build();
    }
}
