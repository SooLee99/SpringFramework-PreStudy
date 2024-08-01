package org.example.springframeworkprestudy.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor()
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Length(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]*$", message = "UserName은 소문자와 숫자만 입력해야 합니다.")
    @Column(unique = true, nullable = false, name = "user_name")
    private String userName;

    @NotNull
    @Length(min = 8, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "비밀번호는 문자와 숫자만 입력해야 합니다.")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    private Timestamp removedAt;

    public User(String userName, String password) {
        this.userName = Objects.requireNonNull(userName, "userName을 작성해주세요.");
        this.password = Objects.requireNonNull(password, "password를 작성해주세요.");
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void updatePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword, "password를 작성해주세요.");
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public void removeUser() {
        this.removedAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
