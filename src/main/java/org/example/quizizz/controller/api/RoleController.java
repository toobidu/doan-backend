package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.PageResponse;
import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;
import org.example.quizizz.service.Interface.IRoleService;
import org.example.quizizz.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_ASSIGNED, response));
    }

    @Operation(summary = "Cập nhật role", description = "Cập nhật thông tin một vai trò theo ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        RoleResponse response = roleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_ASSIGNED, response));
    }

    @Operation(summary = "Xóa role", description = "Xóa một vai trò theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_REMOVED, "Role deleted successfully"));
    }

    @Operation(summary = "Lấy role theo ID", description = "Lấy chi tiết một vai trò theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id) {
        RoleResponse response = roleService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Lấy tất cả role", description = "Lấy danh sách tất cả vai trò trong hệ thống")
    @GetMapping
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        List<RoleResponse> response = roleService.getAll();
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Đếm số lượng role", description = "Lấy tổng số lượng vai trò trong hệ thống")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count() {
        Long count = roleService.count();
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, count));
    }

    @Operation(summary = "Tìm kiếm role", description = "Tìm kiếm và phân trang vai trò")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        Page<RoleResponse> roles = roleService.searchRoles(keyword, PageableUtil.createPageable(page, size, sort));
        PageResponse<RoleResponse> response = PageResponse.of(roles);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, response));
    }

    @Operation(summary = "Lấy quyền của role", description = "Lấy danh sách quyền được gán cho một role")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<List<org.example.quizizz.model.dto.permission.PermissionResponse>>> getRolePermissions(@PathVariable Long roleId) {
        List<org.example.quizizz.model.dto.permission.PermissionResponse> permissions = roleService.getRolePermissions(roleId);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, permissions));
    }

    @Operation(summary = "Cập nhật quyền của role", description = "Cập nhật danh sách quyền cho một role")
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:manage')")
    public ResponseEntity<ApiResponse<String>> updateRolePermissions(
            @PathVariable Long roleId, 
            @RequestBody java.util.Map<String, java.util.List<Long>> request) {
        java.util.List<Long> permissionIds = request.get("permissionIds");
        roleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, "Role permissions updated successfully"));
    }
}
