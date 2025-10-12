package org.example.quizizz.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {
    private String questionText;
    private Long topicId;
    private String questionType;
}
