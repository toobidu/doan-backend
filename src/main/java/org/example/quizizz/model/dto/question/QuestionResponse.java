package org.example.quizizz.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String questionText;
    private Long topicId;
    private String questionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
