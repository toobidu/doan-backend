package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionRequest {
    private Long questionId;
    private String selectedAnswer; // For multiple choice: "A", "B", "C", "D" or text answer
    private Integer selectedOptionIndex; // For multiple choice: 0, 1, 2, 3
    private Long submissionTime; // timestamp when answer was submitted
    private String answerText; // For text-based questions
}
