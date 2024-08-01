package org.example.springframeworkprestudy.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springframeworkprestudy.user.dto.request.LoginRequest;
import org.example.springframeworkprestudy.user.dto.request.SignUpRequest;
import org.example.springframeworkprestudy.user.dto.response.LoginResponse;
import org.example.springframeworkprestudy.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Utility method for performing post requests
    private void performPostRequest(String url, Object request, int expectedStatus, String expectedMessage) throws Exception {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("회원가입을 성공한다.")
    public void 회원가입_성공() throws Exception {
        // given
        String userName = "validUser1";
        String password = "ValidPass123";
        SignUpRequest signUpRequest = new SignUpRequest(userName, password);

        // when & then
        performPostRequest("/api/v1/users/sign-up", signUpRequest, 200, "회원가입에 성공했습니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 이름 중복")
    public void 회원가입_실패_이름_중복() throws Exception {
        // given
        String userName = "duplicateUser";
        String password = "ValidPass123";
        SignUpRequest signUpRequest = new SignUpRequest(userName, password);

        given(userService.signUp(signUpRequest))
                .willThrow(new IllegalArgumentException("이미 존재하는 이름입니다."));

        // when & then
        performPostRequest("/api/v1/users/sign-up", signUpRequest, 400, "이미 존재하는 이름입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 사용자 이름")
    public void 회원가입_실패_유효하지않은_이름() throws Exception {
        String password = "ValidPass123";
        String[] invalidUserNames = {
                "abc",         // 너무 짧음
                "abcdefghijkl",// 너무 김
                "user$",       // 특수 문자 포함
                "User1234",    // 대문자 포함
                "user name"    // 공백 포함
        };

        for (String userName : invalidUserNames) {
            SignUpRequest signUpRequest = new SignUpRequest(userName, password);
            performPostRequest("/api/v1/users/sign-up", signUpRequest, 400, "유효하지 않은 사용자 이름입니다.");
        }
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 비밀번호")
    public void 회원가입_실패_유효하지않은_비밀번호() throws Exception {
        String userName = "validuser1";
        String[] invalidPasswords = {
                "short",         // 너무 짧음
                "veryverylongpassword", // 너무 김
                "nopassword123",  // 숫자만
                "NOLOWERCASE123", // 소문자 없음
                "nouppercase123", // 대문자 없음
                "Valid Pass123",  // 공백 포함
                "Valid@Pass123"   // 특수 문자 포함
        };

        for (String password : invalidPasswords) {
            SignUpRequest signUpRequest = new SignUpRequest(userName, password);
            performPostRequest("/api/v1/users/sign-up", signUpRequest, 400, "유효하지 않은 비밀번호입니다.");
        }
    }

    @Test
    @DisplayName("로그인 성공")
    public void 로그인_성공() throws Exception {
        String userName = "validUser";
        String password = "ValidPass123";
        String token = "token";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        given(userService.login(loginRequest)).willReturn(new LoginResponse(token));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    public void 로그인_실패_잘못된_비밀번호() throws Exception {
        String userName = "validUser";
        String password = "WrongPass123";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        given(userService.login(loginRequest)).willThrow(new IllegalArgumentException("잘못된 비밀번호입니다."));

        performPostRequest("/api/v1/users/login", loginRequest, 401, "잘못된 비밀번호입니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    public void 로그인_실패_사용자_없음() throws Exception {
        String userName = "nonExistentUser";
        String password = "SomePass123";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        given(userService.login(loginRequest)).willThrow(new IllegalArgumentException("사용자가 존재하지 않습니다."));

        performPostRequest("/api/v1/users/login", loginRequest, 401, "사용자가 존재하지 않습니다.");
    }
}
