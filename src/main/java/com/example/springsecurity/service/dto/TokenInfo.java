package com.example.springsecurity.service.dto;

import com.example.springsecurity.entity.Member;
import com.example.springsecurity.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfo {

    private Long memberId;
    private Role role;

    @Builder
    private TokenInfo(Long memberId, Role role) {
        this.memberId = memberId;
        this.role = role;
    }

    public static TokenInfo of(Member member) {
        return TokenInfo.builder()
            .memberId(member.getId())
            .role(member.getRole())
            .build();
    }
}
