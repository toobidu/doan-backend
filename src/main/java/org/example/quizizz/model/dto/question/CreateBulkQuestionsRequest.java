package org.example.quizizz.model.dto.question;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkQuestionsRequest {
    
    @NotEmpty(message = "Questions list cannot be empty")
    @Valid
    private List<CreateQuestionRequest> questions;
}
