package com.example.demo.dtos.auth;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
}
