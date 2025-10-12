package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultResponse {
    private Boolean isCorrect;
    private Integer score;
    private Long timeTaken;
    private Long correctAnswerId;
    private Integer streak;
    private Double streakMultiplier;
}
