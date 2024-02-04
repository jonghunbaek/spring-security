package com.example.springsecurity.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash
public class BlackToken {

    @Id
    private String token;

    @TimeToLive
    private Long expirationSec;

    public BlackToken(String token, Long expirationSec) {
        this.token = token;
        this.expirationSec = expirationSec;
    }
}