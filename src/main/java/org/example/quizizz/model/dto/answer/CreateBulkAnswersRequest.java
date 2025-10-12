package org.example.quizizz.model.dto.answer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateBulkAnswersRequest {
    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<CreateAnswerRequest> answers;
}