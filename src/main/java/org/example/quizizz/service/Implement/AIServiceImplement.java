package org.example.quizizz.service.Implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.model.dto.ai.*;
import org.example.quizizz.model.dto.answer.CreateAnswerRequest;
import org.example.quizizz.model.dto.answer.CreateBulkAnswersRequest;
import org.example.quizizz.model.dto.question.CreateBulkQuestionsRequest;
import org.example.quizizz.model.dto.question.CreateQuestionRequest;
import org.example.quizizz.model.dto.question.QuestionResponse;
import org.example.quizizz.model.dto.question.QuestionWithAnswersResponse;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.service.Interface.IAIService;
import org.example.quizizz.service.Interface.IAnswerService;
import org.example.quizizz.service.Interface.IQuestionService;
import org.example.quizizz.service.Interface.ITopicService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AIServiceImplement implements IAIService {
    
    private final ChatClient chatClient;
    private final ITopicService topicService;
    private final IQuestionService questionService;
    private final IAnswerService answerService;
    private final ObjectMapper objectMapper;
    
    /**
     * Tạo câu hỏi và đáp án từ mô tả tự nhiên
     */
    @Override
    public AIGenerateResponse generateQuestionsFromNaturalLanguage(Long topicId, String userPrompt) {
        log.info("Generating questions for topic {} with prompt: {}", topicId, userPrompt);
        
        TopicResponse topic = topicService.getById(topicId);
        String systemPrompt = buildSystemPrompt(topic, userPrompt);
        
        String jsonResponse = chatClient.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .call()
            .content();
        
        log.debug("AI Response: {}", jsonResponse);
        
        List<QuestionWithAnswersResponse> questions = parseAndSaveQuestions(jsonResponse, topicId);
        
        return AIGenerateResponse.builder()
            .totalGenerated(questions.size())
            .questions(questions)
            .message("Đã tạo thành công " + questions.size() + " câu hỏi")
            .build();
    }
    
    /**
     * Xây dựng system prompt cho AI
     */
    private String buildSystemPrompt(TopicResponse topic, String userPrompt) {
        return """
            Bạn là trợ lý tạo câu hỏi trắc nghiệm cho giáo viên.
            
            THÔNG TIN CHỦ ĐỀ:
            - Tên: %s
            - Mô tả: %s
            
            YÊU CẦU NGƯỜI DÙNG: %s
            
            NHIỆM VỤ:
            1. Phân tích yêu cầu của giáo viên (số lượng, độ khó, nội dung cụ thể)
            2. Tạo câu hỏi trắc nghiệm chất lượng cao
            3. Trả về ĐÚNG format JSON sau (KHÔNG thêm markdown, KHÔNG thêm text khác):
            
            {
              "questions": [
                {
                  "questionText": "Nội dung câu hỏi rõ ràng, chính xác?",
                  "answers": [
                    {
                      "answerText": "Đáp án đúng",
                      "isCorrect": true,
                      "explanation": "Giải thích tại sao đúng"
                    },
                    {
                      "answerText": "Đáp án sai 1",
                      "isCorrect": false,
                      "explanation": "Giải thích tại sao sai"
                    },
                    {
                      "answerText": "Đáp án sai 2",
                      "isCorrect": false,
                      "explanation": "Giải thích tại sao sai"
                    },
                    {
                      "answerText": "Đáp án sai 3",
                      "isCorrect": false,
                      "explanation": "Giải thích tại sao sai"
                    }
                  ]
                }
              ]
            }
            
            QUY TẮC BẮT BUỘC:
            - Mỗi câu hỏi có ĐÚNG 4 đáp án
            - CHỈ 1 đáp án có isCorrect: true
            - Câu hỏi phải liên quan đến chủ đề "%s"
            - Đáp án sai phải hợp lý, không quá dễ loại trừ
            - Có explanation cho TẤT CẢ đáp án
            - Ngôn ngữ: Tiếng Việt, phù hợp học sinh
            - Nếu không nói rõ độ khó, tạo câu hỏi trung bình
            - Nếu không nói rõ số lượng, tạo 10 câu
            """.formatted(topic.getName(), topic.getDescription(), userPrompt, topic.getName());
    }
    
    /**
     * Parse JSON từ AI và lưu vào database
     */
    private List<QuestionWithAnswersResponse> parseAndSaveQuestions(String jsonResponse, Long topicId) {
        try {
            String cleanJson = jsonResponse
                .replaceAll("```json\\n?", "")
                .replaceAll("```\\n?", "")
                .trim();
            
            AIQuestionResponse aiResponse = objectMapper.readValue(cleanJson, AIQuestionResponse.class);
            
            validateAIResponse(aiResponse);
            
            List<CreateQuestionRequest> questionRequests = aiResponse.getQuestions().stream()
                .map(q -> new CreateQuestionRequest(q.getQuestionText(), topicId, "MULTIPLE_CHOICE"))
                .toList();
            
            List<QuestionResponse> savedQuestions = questionService.createBulkQuestions(
                new CreateBulkQuestionsRequest(questionRequests)
            );
            
            for (int i = 0; i < savedQuestions.size(); i++) {
                Long questionId = savedQuestions.get(i).getId();
                List<CreateAnswerRequest> answerRequests = aiResponse.getQuestions().get(i).getAnswers().stream()
                    .map(a -> new CreateAnswerRequest(questionId, a.getAnswerText(), a.getIsCorrect()))
                    .toList();
                
                answerService.createBulkAnswers(new CreateBulkAnswersRequest(answerRequests));
            }
            
            return questionService.getQuestionsByTopicId(topicId);
            
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            throw new RuntimeException("Không thể tạo câu hỏi. Vui lòng thử lại: " + e.getMessage());
        }
    }
    
    /**
     * Validate response từ AI
     */
    private void validateAIResponse(AIQuestionResponse response) {
        if (response.getQuestions() == null || response.getQuestions().isEmpty()) {
            throw new RuntimeException("AI không tạo được câu hỏi");
        }
        
        for (var q : response.getQuestions()) {
            if (q.getAnswers() == null || q.getAnswers().size() != 4) {
                throw new RuntimeException("Mỗi câu hỏi phải có đúng 4 đáp án");
            }
            
            long correctCount = q.getAnswers().stream()
                .filter(AIAnswerData::getIsCorrect)
                .count();
            
            if (correctCount != 1) {
                throw new RuntimeException("Mỗi câu hỏi phải có đúng 1 đáp án đúng");
            }
        }
    }
}
