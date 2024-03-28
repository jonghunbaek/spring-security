package com.example.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class KakaoLoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
