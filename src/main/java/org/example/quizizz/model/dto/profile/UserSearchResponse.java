package org.example.quizizz.model.dto.profile;

import lombok.Data;

@Data
public class UserSearchResponse {
    private Long id;
    private String username;
    private String fullName;
    private String avatarURL;
}
