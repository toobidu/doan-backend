package org.example.quizizz.model.dto.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequest {
    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
