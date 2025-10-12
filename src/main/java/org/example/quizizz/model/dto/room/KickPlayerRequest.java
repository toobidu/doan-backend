package org.example.quizizz.model.dto.room;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KickPlayerRequest {
    @NotNull(message = "Player ID is required")
    private Long playerId;

    private String reason;
}
