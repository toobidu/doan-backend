package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.permission.CreatePermissionRequest;
import org.example.quizizz.model.dto.permission.PermissionResponse;
import org.example.quizizz.model.dto.permission.UpdatePermissionRequest;
import org.example.quizizz.service.Interface.IPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission", description = "APIs liên quan đến quyền")
public class PermissionController {

    private final IPermissionService permissionService;

    @Operation(summary = "Tạo mới permission", description = "Tạo mới một quyền trong hệ thống")
    @PostMapping
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Valid @RequestBody CreatePermissionRequest request) {
        PermissionResponse response = permissionService.create(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PERMISSION_GRANTED, response));
    }

    @Operation(summary = "Cập nhật permission", description = "Cập nhật thông tin một quyền theo ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @Parameter(description = "ID của permission") @PathVariable Long id,
            @RequestBody UpdatePermissionRequest request) {
        PermissionResponse response = permissionService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PERMISSION_GRANTED, response));
    }

    @Operation(summary = "Xóa permission", description = "Xóa một quyền theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseEntity<ApiResponse<String>> delete(
            @Parameter(description = "ID của permission") @PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PERMISSION_REVOKED, "Permission deleted successfully"));
    }

    @Operation(summary = "Lấy permission theo ID", description = "Lấy chi tiết một quyền theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseEntity<ApiResponse<PermissionResponse>> getById(
            @Parameter(description = "ID của permission") @PathVariable Long id) {
        PermissionResponse response = permissionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Lấy tất cả permission", description = "Lấy danh sách tất cả quyền trong hệ thống")
    @GetMapping
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll() {
        List<PermissionResponse> response = permissionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }
}
