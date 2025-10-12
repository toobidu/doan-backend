package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.permission.AssignRolesToPermissionRequest;
import org.example.quizizz.model.dto.role.AssignPermissionsToRoleRequest;

import java.util.Set;

public interface IRolePermissionService {
    void assignPermissionsToRole(AssignPermissionsToRoleRequest request);
    void removePermissionsFromRole(AssignPermissionsToRoleRequest request);
    void assignRolesToPermission(AssignRolesToPermissionRequest request);
    void removeRolesFromPermission(AssignRolesToPermissionRequest request);
    void refreshUserPermissionsCache(Set<Long> userIds);
}
