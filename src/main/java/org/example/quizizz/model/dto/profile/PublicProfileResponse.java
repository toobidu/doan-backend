package org.example.quizizz.model.dto.profile;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private java.time.LocalDate dob;
    private String avatarURL;
    private LocalDateTime createdAt;
}
