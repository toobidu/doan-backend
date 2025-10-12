package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextQuestionResponse {
    private Long questionId;
    private String questionText;
    private List<AnswerOption> answers;
    private Integer timeLimit;
    private Integer questionNumber;
    private Integer totalQuestions;


}
