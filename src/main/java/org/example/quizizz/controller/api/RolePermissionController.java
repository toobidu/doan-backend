package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.permission.AssignRolesToPermissionRequest;
import org.example.quizizz.model.dto.role.AssignPermissionsToRoleRequest;
import org.example.quizizz.service.Interface.IRolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role-permissions")
@RequiredArgsConstructor
@Tag(name = "3. RolePermission", description = "APIs liên quan đến gán quyền cho vai trò")
public class RolePermissionController {

    private final IRolePermissionService rolePermissionService;

    @Operation(summary = "Gán quyền cho role", description = "Gán một hoặc nhiều quyền cho một role")
    @PostMapping("/assign-permissions-to-role")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<ApiResponse<String>> assignPermissionsToRole(@Valid @RequestBody AssignPermissionsToRoleRequest request) {
        rolePermissionService.assignPermissionsToRole(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PERMISSION_GRANTED, "Permissions assigned to role successfully"));
    }

    @Operation(summary = "Xóa quyền khỏi role", description = "Xóa một hoặc nhiều quyền khỏi một role")
    @DeleteMapping("/remove-permissions-from-role")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<ApiResponse<String>> removePermissionsFromRole(@Valid @RequestBody AssignPermissionsToRoleRequest request) {
        rolePermissionService.removePermissionsFromRole(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.PERMISSION_REVOKED, "Permissions removed from role successfully"));
    }

    @Operation(summary = "Gán role cho permission", description = "Gán một hoặc nhiều role cho một permission")
    @PostMapping("/assign-roles-to-permission")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<ApiResponse<String>> assignRolesToPermission(@Valid @RequestBody AssignRolesToPermissionRequest request) {
        rolePermissionService.assignRolesToPermission(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_ASSIGNED, "Roles assigned to permission successfully"));
    }

    @Operation(summary = "Xóa role khỏi permission", description = "Xóa một hoặc nhiều role khỏi một permission")
    @DeleteMapping("/remove-roles-from-permission")
    @PreAuthorize("hasAuthority('user:manage')")
    public ResponseEntity<ApiResponse<String>> removeRolesFromPermission(@Valid @RequestBody AssignRolesToPermissionRequest request) {
        rolePermissionService.removeRolesFromPermission(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.ROLE_REMOVED, "Roles removed from permission successfully"));
    }
}
