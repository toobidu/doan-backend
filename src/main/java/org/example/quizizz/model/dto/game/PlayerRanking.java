package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRanking {
    private Integer rank;
    private Long userId;
    private String userName;
    private String avatar;
    private Integer totalScore;
    private Long totalTime;
    private Integer correctAnswers;
    private Integer totalAnswers;
}
