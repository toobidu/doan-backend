package org.example.quizizz.model.dto.question;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBulkQuestionsRequest {
    
    @NotEmpty(message = "Question IDs list cannot be empty")
    private List<Long> questionIds;
}
