package org.example.quizizz.model.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitePlayerRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Username is required")
    private String username;

    private String message;
}
