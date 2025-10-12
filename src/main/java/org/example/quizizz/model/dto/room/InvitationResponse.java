package org.example.quizizz.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    private Long id;
    private Long roomId;
    private String roomName;
    private Long inviterId;
    private String inviterUsername;
    private Long inviteeId;
    private String inviteeUsername;
    private String status; // PENDING, ACCEPTED, DECLINED
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
