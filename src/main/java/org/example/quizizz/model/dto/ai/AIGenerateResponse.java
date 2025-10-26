package org.example.quizizz.model.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quizizz.model.dto.question.QuestionWithAnswersResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateResponse {
    
    private Integer totalGenerated;
    private List<QuestionWithAnswersResponse> questions;
    private String message;
}
