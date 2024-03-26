package com.example.springsecurity.controller;

import com.example.springsecurity.entity.BlackToken;
import com.example.springsecurity.repository.BlackTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HomeController {

    private final BlackTokenRepository blackTokenRepository;

    @RequestMapping("/home")
    public String home() {
        return "디스 이즈 어 홈";
    }

    @GetMapping("/shop")
    public String shop(String idx) {
        blackTokenRepository.save(new BlackToken("t" + idx, "t"+idx, 100L));
        return "디스 이즈 어 샵";
    }

    @GetMapping("/check")
    public String check(String id) {
        BlackToken blackToken = blackTokenRepository.findById(id).get();
        log.info("token :: {}", blackToken.getToken() + " " + blackToken.getExpirationSec());

        return "확인";
    }

    @RequestMapping("/role/user")
    public String role() {
        return "user인증 완료";
    }

    @RequestMapping("/role/admin")
    public String roleAdmin() {
        return "admin인증 완료";
    }
}
