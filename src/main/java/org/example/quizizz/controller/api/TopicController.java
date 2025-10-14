package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.topic.CreateTopicRequest;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.model.dto.topic.UpdateTopicRequest;
import org.example.quizizz.service.Interface.ITopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "5. Topic", description = "APIs liên quan đến chủ đề")
public class TopicController {
    private final ITopicService topicService;

    /**
     * Tạo mới chủ đề.
     * @param request Thông tin chủ đề cần tạo
     * @return Thông tin chủ đề vừa tạo
     */
    @Operation(summary = "Tạo mới chủ đề", description = "Tạo mới một chủ đề trong hệ thống")
    @PostMapping
    @PreAuthorize("hasAuthority('topic:manage')")
    public ResponseEntity<ApiResponse<TopicResponse>> create(@Valid @RequestBody CreateTopicRequest request) {
        TopicResponse response = topicService.create(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.TOPIC_CREATED, response));
    }

    /**
     * Cập nhật thông tin chủ đề.
     * @param id Id chủ đề
     * @param request Thông tin cập nhật
     * @return Thông tin chủ đề sau cập nhật
     */
    @Operation(summary = "Cập nhật chủ đề", description = "Cập nhật thông tin một chủ đề theo ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('topic:manage')")
    public ResponseEntity<ApiResponse<TopicResponse>> update(@PathVariable Long id, @RequestBody UpdateTopicRequest request) {
        TopicResponse response = topicService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.TOPIC_UPDATED, response));
    }

    /**
     * Xóa chủ đề.
     * @param id Id chủ đề
     */
    @Operation(summary = "Xóa chủ đề", description = "Xóa một chủ đề theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('topic:manage')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.TOPIC_DELETED, "Topic deleted successfully"));
    }

    /**
     * Lấy thông tin chủ đề theo id.
     * @param id Id chủ đề
     * @return Thông tin chủ đề
     */
    @Operation(summary = "Lấy chủ đề theo ID", description = "Lấy chi tiết một chủ đề theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('topic:manage')")
    public ResponseEntity<ApiResponse<TopicResponse>> getById(@PathVariable Long id) {
        TopicResponse response = topicService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    /**
     * Lấy danh sách tất cả chủ đề.
     * @return Danh sách chủ đề
     */
    @Operation(summary = "Lấy tất cả chủ đề", description = "Lấy danh sách tất cả chủ đề trong hệ thống")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getAll() {
        List<TopicResponse> response = topicService.getAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
