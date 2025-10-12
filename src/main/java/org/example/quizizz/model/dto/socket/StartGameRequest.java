package org.example.quizizz.model.dto.socket;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonSetter;

@Data
public class StartGameRequest {
    private Long roomId;

    // ✅ FIX: Handle both Integer and Long from JavaScript
    @JsonSetter("roomId")
    public void setRoomId(Object roomId) {
        if (roomId instanceof Integer) {
            this.roomId = ((Integer) roomId).longValue();
        } else if (roomId instanceof Long) {
            this.roomId = (Long) roomId;
        } else if (roomId instanceof Number) {
            this.roomId = ((Number) roomId).longValue();
        } else if (roomId instanceof String) {
            this.roomId = Long.parseLong((String) roomId);
        }
    }
}
