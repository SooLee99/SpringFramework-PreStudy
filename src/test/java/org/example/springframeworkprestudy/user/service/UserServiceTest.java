package org.example.springframeworkprestudy.user.service;

import org.example.springframeworkprestudy.user.dto.request.LoginRequest;
import org.example.springframeworkprestudy.user.dto.request.SignUpRequest;
import org.example.springframeworkprestudy.user.dto.response.LoginResponse;
import org.example.springframeworkprestudy.user.dto.response.SignUpResponse;
import org.example.springframeworkprestudy.user.entity.User;
import org.example.springframeworkprestudy.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    public void testSignUpSuccess() {
        // given
        String userName = "validUser";
        String rawPassword = "ValidPass123";
        String encodedPassword = "encodedPassword";

        given(userRepository.findByUserName(userName)).willReturn(Optional.empty());
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        SignUpResponse result = userService.signUp(new SignUpRequest(userName, rawPassword));

        // then
        assertNotNull(result);
        assertEquals(userName, result.getUserName());
        assertEquals(encodedPassword, result.getPassword());
        assertNotEquals(rawPassword, result.getPassword()); // 원래 비밀번호와 저장된 비밀번호는 다름
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자 이름")
    public void testSignUpFailDueToDuplicateUserName() {
        // given
        String userName = "existingUser";
        String rawPassword = "ValidPass123";
        SignUpRequest signUpRequest = new SignUpRequest(userName, rawPassword);

        given(userRepository.findByUserName(userName)).willReturn(Optional.of(new User()));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(signUpRequest);
        });

        assertEquals("이미 존재하는 이름입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    public void testLoginSuccess() {
        // given
        String userName = "validUser";
        String rawPassword = "ValidPass123";
        String encodedPassword = "encodedPassword";
        LoginRequest loginRequest = new LoginRequest(userName, rawPassword);
        User user = new User(userName, encodedPassword);

        given(userRepository.findByUserName(userName)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when
        String token = String.valueOf(userService.login(loginRequest));

        // then
        assertNotNull(token); // 토큰이 생성됨
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    public void testLoginFailDueToIncorrectPassword() {
        // given
        String userName = "validUser";
        String rawPassword = "WrongPass123";
        String encodedPassword = "encodedPassword";
        LoginRequest loginRequest = new LoginRequest(userName, rawPassword);
        User user = new User(userName, encodedPassword);

        given(userRepository.findByUserName(userName)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    public void testLoginFailDueToNonExistentUser() {
        // given
        String userName = "nonExistentUser";
        String rawPassword = "SomePass123";
        LoginRequest loginRequest = new LoginRequest(userName, rawPassword);

        given(userRepository.findByUserName(userName)).willReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }
}
