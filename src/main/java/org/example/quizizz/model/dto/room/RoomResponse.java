package org.example.quizizz.model.dto.room;

import org.example.quizizz.common.constants.RoomMode;
import org.example.quizizz.common.constants.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomCode;
    private String roomName;
    private RoomMode roomMode;
    private Long topicId;
    private String topicName;
    private Boolean isPrivate;
    private Long ownerId;
    private String ownerUsername;
    private RoomStatus status;
    private Integer maxPlayers;
    private Integer currentPlayers;
    private Integer questionCount;
    private Integer countdownTime;
    private LocalDateTime createdAt;
}
