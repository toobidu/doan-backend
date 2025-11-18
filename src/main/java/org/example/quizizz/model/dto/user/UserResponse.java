package org.example.quizizz.model.dto.user;

import org.example.quizizz.model.dto.role.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dob;
    private String avatarURL;
    private String typeAccount;
    private Boolean online;
    private LocalDateTime lastOnlineTime;
    private Boolean emailVerified;
    private String systemFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoleResponse> roles;
}