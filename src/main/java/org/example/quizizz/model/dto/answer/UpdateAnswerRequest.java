package org.example.quizizz.model.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnswerRequest {
    private String answerText;
    private Boolean isCorrect;
}
