package com.example.springsecurity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/home")
    public String home() {
        return "디스 이즈 어 홈";
    }

    @RequestMapping("/shop")
    public String shop() {
        return "디스 이즈 어 샵";
    }
}
