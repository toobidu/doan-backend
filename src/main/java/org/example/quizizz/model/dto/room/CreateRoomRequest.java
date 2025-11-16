package org.example.quizizz.model.dto.room;

import org.example.quizizz.common.constants.RoomMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    @NotBlank(message = "Room name is required")
    private String roomName;

    @NotNull(message = "Room mode is required")
    private RoomMode roomMode;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotNull(message = "Exam ID is required")
    private Long examId;

    private Boolean isPrivate = false;
    private Integer maxPlayers;
    private Integer questionCount = 10;
    private Integer countdownTime = 30;
}
