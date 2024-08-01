package org.example.springframeworkprestudy.user.service;

import org.example.springframeworkprestudy.user.dto.request.LoginRequest;
import org.example.springframeworkprestudy.user.dto.request.SignUpRequest;
import org.example.springframeworkprestudy.user.dto.response.LoginResponse;
import org.example.springframeworkprestudy.user.dto.response.SignUpResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        // TODO: Implement the methods
        return new SignUpResponse(signUpRequest.getUserName(), signUpRequest.getPassword());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // TODO: Implement the methods
        return new LoginResponse("");
    }
}
