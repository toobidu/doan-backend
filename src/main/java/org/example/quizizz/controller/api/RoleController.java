package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;
import org.example.quizizz.service.Interface.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "2. Role", description = "APIs liên quan đến vai trò")
public class RoleController {

    private final IRoleService roleService;

    @Operation(summary = "Tạo mới role", description = "Tạo mới một vai trò (role) trong hệ thống")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_FULL_ACCESS')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_ASSIGNED, response));
    }

    @Operation(summary = "Cập nhật role", description = "Cập nhật thông tin một vai trò theo ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_FULL_ACCESS')")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_ASSIGNED, response));
    }

    @Operation(summary = "Xóa role", description = "Xóa một vai trò theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_FULL_ACCESS')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_REMOVED, "Role deleted successfully"));
    }

    @Operation(summary = "Lấy role theo ID", description = "Lấy chi tiết một vai trò theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_FULL_ACCESS')")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id) {
        RoleResponse response = roleService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Lấy tất cả role", description = "Lấy danh sách tất cả vai trò trong hệ thống")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_FULL_ACCESS')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        List<RoleResponse> response = roleService.getAll();
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }
}
