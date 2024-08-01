package org.example.springframeworkprestudy.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.springframeworkprestudy.user.dto.request.LoginRequest;
import org.example.springframeworkprestudy.user.dto.request.SignUpRequest;
import org.example.springframeworkprestudy.user.dto.response.LoginResponse;
import org.example.springframeworkprestudy.user.dto.response.SignUpResponse;
import org.example.springframeworkprestudy.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        return userService.signUp(signUpRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }



}
