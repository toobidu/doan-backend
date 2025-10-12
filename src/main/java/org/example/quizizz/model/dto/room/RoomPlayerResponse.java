package org.example.quizizz.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPlayerResponse {
    private Long id;
    private Long userId;
    private String username;
    private Boolean isHost;
    private LocalDateTime joinedAt;
}
