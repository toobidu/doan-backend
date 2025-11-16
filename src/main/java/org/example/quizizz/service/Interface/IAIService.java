package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.ai.AIGenerateResponse;

public interface IAIService {
    
    /**
     * Tạo câu hỏi và đáp án từ mô tả tự nhiên
     */
    AIGenerateResponse generateQuestionsFromNaturalLanguage(Long topicId, Long examId, String userPrompt);
}
