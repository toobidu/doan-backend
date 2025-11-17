package org.example.quizizz.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.ai.AIGenerateRequest;
import org.example.quizizz.model.dto.ai.AIGenerateResponse;
import org.example.quizizz.service.Interface.IAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "8. AI Assistant", description = "APIs tạo câu hỏi tự động bằng AI")
public class AIController {
    
    private final IAIService aiServiceImpl;
    
    /**
     * Tạo câu hỏi và đáp án bằng AI
     */
    @PostMapping("/generate-questions")
    @PreAuthorize("hasAuthority('question:manage')")
    @Operation(
        summary = "Tạo câu hỏi + đáp án bằng AI",
        description = "Giáo viên nhập mô tả tự nhiên, AI tự động tạo câu hỏi và đáp án"
    )
    public ResponseEntity<ApiResponse<AIGenerateResponse>> generateQuestions(
            @RequestBody @Valid AIGenerateRequest request) {
        
        AIGenerateResponse response = aiServiceImpl.generateQuestionsFromNaturalLanguage(
            request.getExamId(),
            request.getUserPrompt()
        );
        
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }
}
