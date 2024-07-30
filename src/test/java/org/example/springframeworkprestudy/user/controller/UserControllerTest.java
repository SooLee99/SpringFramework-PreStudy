package org.example.springframeworkprestudy.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springframeworkprestudy.user.dto.request.LoginRequest;
import org.example.springframeworkprestudy.user.dto.request.SignUpRequest;
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

    /**
     * 회원가입 API 테스트
     *  - username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성되어야 한다.
     *  - password는 최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9)로 구성되어야 한다.
     */

    @Test
    @DisplayName("회원가입을 성공한다.")
    public void 회원가입_성공() throws Exception {
        // given
        String userName = "validUser1"; // 유효한 사용자 이름
        String password = "ValidPass123"; // 유효한 비밀번호
        SignUpRequest signUpRequest = new SignUpRequest(userName, password);

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value(userName))
                .andExpect(jsonPath("$.data.password").value(password));
    }

    @Test
    @DisplayName("회원가입 실패 - 이름 중복")
    public void 회원가입_실패_이름_중복() throws Exception {
        // given
        String userName = "duplicateUser"; // 이미 존재하는 사용자 이름
        String password = "ValidPass123"; // 유효한 비밀번호
        SignUpRequest signUpRequest = new SignUpRequest(userName, password);

        given(userService.signUp(userName, password))
                .willThrow(new IllegalArgumentException("이미 존재하는 이름입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이름입니다."));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 사용자 이름")
    public void 회원가입_실패_유효하지않은_이름() throws Exception {
        // given
        String[] invalidUserNames = {
                "abc",         // 너무 짧음
                "abcdefghijkl",// 너무 김
                "user$",       // 특수 문자 포함
                "User1234",    // 대문자 포함
                "user name",   // 공백 포함
        };

        for (String userName : invalidUserNames) {
            String password = "ValidPass123"; // 유효한 비밀번호
            SignUpRequest signUpRequest = new SignUpRequest(userName, password);

            // when & then
            mockMvc.perform(post("/api/v1/users/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(signUpRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("유효하지 않은 사용자 이름입니다."));
        }
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 비밀번호")
    public void 회원가입_실패_유효하지않은_비밀번호() throws Exception {
        // given
        String userName = "validuser1"; // 유효한 사용자 이름

        String[] invalidPasswords = {
                "short",        // 너무 짧음
                "veryverylongpassword", // 너무 김
                "nopassword123", // 숫자만
                "NOLOWERCASE123", // 소문자 없음
                "nouppercase123", // 대문자 없음
                "Valid Pass123", // 공백 포함
                "Valid@Pass123"  // 특수 문자 포함
        };

        for (String password : invalidPasswords) {
            SignUpRequest signUpRequest = new SignUpRequest(userName, password);

            // when & then
            mockMvc.perform(post("/api/v1/users/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(signUpRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("유효하지 않은 비밀번호입니다."));
        }
    }

    /**
     * 로그인 API 테스트
     *  - DB에서 username을 사용하여 저장된 회원의 유무를 확인하고 있다면 password 비교하기
     *  - 로그인 성공 시, 로그인에 성공한 유저의 정보와 JWT를 활용하여 토큰을 발급하고,
     *    발급한 토큰을 Header에 추가하고 성공했다는 메시지, 상태코드 와 함께 Client에 반환하기
     */

    @Test
    @DisplayName("로그인 성공")
    public void 로그인_성공() throws Exception {
        // given
        String userName = "validUser";
        String password = "ValidPass123";
        String token = "jwt.token";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        // UserService.login 메서드가 'token' 문자열을 반환하도록 Mock 설정
        given(userService.login(userName, password)).willReturn(token);

        // when & then
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
        // given
        String userName = "validUser";
        String password = "WrongPass123";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        given(userService.login(userName, password)).willThrow(new IllegalArgumentException("잘못된 비밀번호입니다."));

        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("잘못된 비밀번호입니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    public void 로그인_실패_사용자_없음() throws Exception {
        // given
        String userName = "nonExistentUser";
        String password = "SomePass123";
        LoginRequest loginRequest = new LoginRequest(userName, password);

        given(userService.login(anyString(), anyString())).willThrow(new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("사용자가 존재하지 않습니다."));
    }
}
