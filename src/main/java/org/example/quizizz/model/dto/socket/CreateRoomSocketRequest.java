package org.example.quizizz.model.dto.socket;

import org.example.quizizz.common.constants.RoomMode;
import lombok.Data;

@Data
public class CreateRoomSocketRequest {
    private String roomName;
    private RoomMode roomMode;
    private Long topicId;
    private Long examId;
    private Boolean isPrivate = false;
    private Integer maxPlayers;
    private Integer questionCount = 10;
    private Integer countdownTime = 10;
}
