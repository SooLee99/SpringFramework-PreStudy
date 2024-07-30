package org.example.springframeworkprestudy.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {
    private String userName;
    private String password;
}
