package org.example.springframeworkprestudy.user.repository;

import org.example.springframeworkprestudy.user.entity.User;
import org.example.springframeworkprestudy.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 가입을 시도한다.")
    public void testSignUp() {
        // given
        String userName = "newUser";
        String password = "newPassword";
        User user = new User(userName, password);

        // when
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByUserName(userName);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(userName, foundUser.get().getUserName());
    }

    @Test
    @DisplayName("로그인을 시도한다.")
    public void testLogin() {
        // given
        String userName = "loginUser";
        String password = "loginPassword";
        User user = new User(userName, password);
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByUserName(userName);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(userName, foundUser.get().getUserName());
        assertEquals(password, foundUser.get().getPassword());
    }
}
