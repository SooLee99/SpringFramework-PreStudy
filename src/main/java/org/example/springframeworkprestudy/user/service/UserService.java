package org.example.springframeworkprestudy.user.service;

import org.example.springframeworkprestudy.user.dto.response.LoginResponse;
import org.example.springframeworkprestudy.user.dto.response.SignUpResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public SignUpResponse signUp(String userName, String password) {
        // TODO: Implement the methods
        return new SignUpResponse(userName, password);
    }

    public String login(String userName, String password) {
        // TODO: Implement the methods
        return "";
    }
}
