package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;
import org.example.quizizz.model.dto.permission.PermissionResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse create(CreateRoleRequest request);
    RoleResponse update(Long id, UpdateRoleRequest request);
    void delete(Long id);
    RoleResponse getById(Long id);
    List<RoleResponse> getAll();
    Long count();
    org.springframework.data.domain.Page<RoleResponse> searchRoles(String keyword, org.springframework.data.domain.Pageable pageable);
    List<PermissionResponse> getRolePermissions(Long roleId);
    void updateRolePermissions(Long roleId, List<Long> permissionIds);
}
