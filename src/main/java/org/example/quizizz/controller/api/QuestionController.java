package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.question.*;
import org.example.quizizz.service.Interface.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "6. Question", description = "APIs liên quan đến câu hỏi")
public class QuestionController {

    private final IQuestionService questionService;

    @Operation(summary = "Lấy câu hỏi ngẫu nhiên với đáp án", description = "Lấy câu hỏi ngẫu nhiên kèm đáp án theo topic và loại câu hỏi")
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<List<QuestionWithAnswersResponse>>> getRandomQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "10") int count) {

        List<QuestionWithAnswersResponse> questions = questionService.getRandomQuestionsWithAnswers(topicId, questionType, count);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, questions));
    }

    @Operation(summary = "Lấy câu hỏi cho người chơi cụ thể", description = "Lấy câu hỏi ngẫu nhiên khác nhau cho mỗi người chơi")
    @GetMapping("/random/player/{playerId}")
    public ResponseEntity<ApiResponse<List<QuestionWithAnswersResponse>>> getRandomQuestionsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "10") int count) {

        List<QuestionWithAnswersResponse> questions = questionService.getRandomQuestionsForPlayer(topicId, questionType, count, playerId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, questions));
    }

    @Operation(summary = "Đếm số câu hỏi có sẵn", description = "Đếm số câu hỏi có sẵn theo topic và loại câu hỏi")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countAvailableQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType) {

        long count = questionService.countAvailableQuestions(topicId, questionType);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, count));
    }

    @Operation(summary = "Tạo câu hỏi mới", description = "Tạo một câu hỏi mới")
    @PostMapping
    @PreAuthorize("hasAuthority('question:manage')")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Tạo nhiều câu hỏi", description = "Tạo nhiều câu hỏi cùng lúc")
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('question:manage')")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> createBulkQuestions(@Valid @RequestBody CreateBulkQuestionsRequest request) {
        List<QuestionResponse> responses = questionService.createBulkQuestions(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, responses));
    }

    @Operation(summary = "Cập nhật câu hỏi", description = "Cập nhật thông tin câu hỏi theo ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('question:manage')")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @RequestBody UpdateQuestionRequest request) {
        QuestionResponse response = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Xóa câu hỏi", description = "Xóa câu hỏi theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('question:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, null));
    }

    @Operation(summary = "Xóa nhiều câu hỏi", description = "Xóa nhiều câu hỏi cùng lúc")
    @DeleteMapping("/bulk")
    @PreAuthorize("hasAuthority('question:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteBulkQuestions(@Valid @RequestBody DeleteBulkQuestionsRequest request) {
        questionService.deleteBulkQuestions(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, null));
    }

    @Operation(summary = "Lấy câu hỏi theo ID", description = "Lấy thông tin chi tiết câu hỏi theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Lấy câu hỏi theo topic", description = "Lấy tất cả câu hỏi và đáp án theo topic ID")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<QuestionWithAnswersResponse>>> getQuestionsByTopicId(@PathVariable Long topicId) {
        List<QuestionWithAnswersResponse> questions = questionService.getQuestionsByTopicId(topicId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, questions));
    }
}
