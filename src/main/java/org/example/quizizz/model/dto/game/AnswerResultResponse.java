package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultResponse {
    private Boolean isCorrect;
    private String correctAnswer;
    private String userAnswer;
    private Integer pointsEarned;
    private Integer totalScore;
    private Long responseTime; // milliseconds
    private String explanation;
    private Integer rank; // current rank after this answer
}
