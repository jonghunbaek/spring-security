package com.example.springsecurity.entity;

import com.example.springsecurity.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.springsecurity.entity.Role.valueOf;

@NoArgsConstructor
@Getter @Setter
@Table(name = "users")
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private Member(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Member from(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder) {
        return Member.builder()
            .name(signUpRequest.getName())
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .role(valueOf(signUpRequest.getRole()))
            .build();
    }

    public void validatePassword(String requestPassword, PasswordEncoder passwordEncoder) {
        if (isNotMatch(requestPassword, passwordEncoder)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private boolean isNotMatch(String requestPassword, PasswordEncoder passwordEncoder) {
        return !passwordEncoder.matches(requestPassword, this.password);
    }
}
