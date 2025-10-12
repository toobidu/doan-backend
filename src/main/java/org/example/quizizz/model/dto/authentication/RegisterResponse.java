package org.example.quizizz.model.dto.authentication;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
}
