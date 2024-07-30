package org.example.springframeworkprestudy.user.entity;

import java.sql.Timestamp;

public class User {
    private Integer id;
    private String userName;
    private String password;
    private UserRole role = UserRole.USER;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp removedAt;
}
