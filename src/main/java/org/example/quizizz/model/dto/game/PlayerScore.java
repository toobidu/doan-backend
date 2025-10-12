package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerScore {
    private Long userId;
    private String userName;
    private String avatar;
    private Integer score;
    private Long timeTaken;
}
