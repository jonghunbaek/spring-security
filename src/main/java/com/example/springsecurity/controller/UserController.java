package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SignIn;
import com.example.springsecurity.dto.SignUp;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repo.RoleRepository;
import com.example.springsecurity.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/security")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/sign-in")
    public ResponseEntity<String> authenticateUser(@RequestBody SignIn signIn) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signIn.getUsername(), signIn.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> joinMember(@RequestBody SignUp signUp)  {
        User user = User.builder()
            .name(signUp.getName())
            .userName(signUp.getUsername())
            .email(signUp.getEmail())
            .password(passwordEncoder.encode(signUp.getPassword()))
            .build();

        Role role = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Set.of(role));
        userRepository.save(user);

        return new ResponseEntity<>("회원 가입 성공", HttpStatus.OK);
    }
}
