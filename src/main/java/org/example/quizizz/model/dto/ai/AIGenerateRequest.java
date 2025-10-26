package org.example.quizizz.model.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateRequest {
    
    @NotNull(message = "Topic ID không được để trống")
    private Long topicId;
    
    @NotBlank(message = "Vui lòng mô tả câu hỏi bạn muốn tạo")
    @Size(min = 10, max = 500, message = "Mô tả từ 10-500 ký tự")
    private String userPrompt;
}
