package org.example.quizizz.model.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnswerData {
    
    private String answerText;
    private Boolean isCorrect;
    private String explanation;
}
