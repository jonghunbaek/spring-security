package com.example.springsecurity.repository;

import com.example.springsecurity.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByToken(String refreshToken);

    Optional<RefreshToken> findByToken(String refreshToken);
}
