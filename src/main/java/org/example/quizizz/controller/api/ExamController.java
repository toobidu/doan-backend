package org.example.quizizz.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.PageResponse;
import org.example.quizizz.model.dto.exam.CreateExamRequest;
import org.example.quizizz.model.dto.exam.ExamResponse;
import org.example.quizizz.model.dto.exam.UpdateExamRequest;
import org.example.quizizz.service.Interface.IExamService;
import org.example.quizizz.util.PageableUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Tag(name = "Exam", description = "APIs liên quan đến đề thi")
public class ExamController {

    private final IExamService examService;

    @PostMapping
    @PreAuthorize("hasAuthority('topic:manage')")
    @Operation(summary = "Tạo đề thi mới", description = "Tạo một đề thi mới thuộc chủ đề")
    public ResponseEntity<ApiResponse<ExamResponse>> create(
            @Valid @RequestBody CreateExamRequest request,
            Authentication authentication) {
        Long teacherId = (Long) authentication.getPrincipal();
        ExamResponse response = examService.create(request, teacherId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('topic:manage')")
    @Operation(summary = "Cập nhật đề thi", description = "Cập nhật thông tin đề thi theo ID")
    public ResponseEntity<ApiResponse<ExamResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateExamRequest request) {
        ExamResponse response = examService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('topic:manage')")
    @Operation(summary = "Xóa đề thi", description = "Xóa đề thi và tất cả câu hỏi, đáp án của đề thi")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy đề thi theo ID", description = "Lấy thông tin chi tiết đề thi theo ID")
    public ResponseEntity<ApiResponse<ExamResponse>> getById(@PathVariable Long id) {
        ExamResponse response = examService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả đề thi", description = "Lấy danh sách tất cả đề thi")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getAll() {
        List<ExamResponse> response = examService.getAll();
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Lấy đề thi theo chủ đề", description = "Lấy danh sách đề thi thuộc một chủ đề")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getByTopicId(@PathVariable Long topicId) {
        List<ExamResponse> response = examService.getByTopicId(topicId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm đề thi", description = "Tìm kiếm và lọc đề thi với phân trang")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        PageResponse<ExamResponse> response = PageResponse.of(
                examService.search(keyword, topicId, PageableUtil.createPageable(page, size, sort)));
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }
}
