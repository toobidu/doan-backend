package org.example.quizizz.model.dto.answer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkAnswersRequest {
    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<CreateAnswerRequest> answers;
}