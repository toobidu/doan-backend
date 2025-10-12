package org.example.quizizz.service.Implement;

import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.common.constants.PermissionCode;
import org.example.quizizz.common.exception.ApiException;
import org.example.quizizz.model.dto.permission.AssignRolesToPermissionRequest;
import org.example.quizizz.model.dto.role.AssignPermissionsToRoleRequest;
import org.example.quizizz.model.entity.Permission;
import org.example.quizizz.model.entity.Role;
import org.example.quizizz.model.entity.RolePermission;
import org.example.quizizz.repository.PermissionRepository;
import org.example.quizizz.repository.RolePermissionRepository;
import org.example.quizizz.repository.RoleRepository;
import org.example.quizizz.service.Interface.IRedisService;
import org.example.quizizz.service.Interface.IRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionServiceImplement implements IRolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final IRedisService redisService;

    @Override
    public void assignPermissionsToRole(AssignPermissionsToRoleRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        if (permissions.size() != request.getPermissionIds().size()) {
            throw new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "One or more permissions not found");
        }

        List<RolePermission> existingAssignments = rolePermissionRepository
                .findByRoleIdAndPermissionIdIn(request.getRoleId(), request.getPermissionIds());
        Set<Long> existingPermissionIds = existingAssignments.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());

        Set<Long> newPermissionIds = request.getPermissionIds().stream()
                .filter(id -> !existingPermissionIds.contains(id))
                .collect(Collectors.toSet());

        List<RolePermission> newAssignments = newPermissionIds.stream()
                .map(permissionId -> {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(request.getRoleId());
                    rp.setPermissionId(permissionId);
                    return rp;
                })
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(newAssignments);

        List<Long> affectedUserIds = rolePermissionRepository.findUserIdsByRoleId(request.getRoleId());
        refreshUserPermissionsCache(Set.copyOf(affectedUserIds));
    }

    @Override
    public void removePermissionsFromRole(AssignPermissionsToRoleRequest request) {
        roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));

        List<Long> affectedUserIds = rolePermissionRepository.findUserIdsByRoleId(request.getRoleId());

        rolePermissionRepository.deleteByRoleIdAndPermissionIdIn(request.getRoleId(), request.getPermissionIds());

        refreshUserPermissionsCache(Set.copyOf(affectedUserIds));
    }

    @Override
    public void assignRolesToPermission(AssignRolesToPermissionRequest request) {
        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Permission not found"));

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "One or more roles not found");
        }

        List<RolePermission> existingAssignments = rolePermissionRepository
                .findByPermissionIdAndRoleIdIn(request.getPermissionId(), request.getRoleIds());
        Set<Long> existingRoleIds = existingAssignments.stream()
                .map(RolePermission::getRoleId)
                .collect(Collectors.toSet());

        Set<Long> newRoleIds = request.getRoleIds().stream()
                .filter(id -> !existingRoleIds.contains(id))
                .collect(Collectors.toSet());

        List<RolePermission> newAssignments = newRoleIds.stream()
                .map(roleId -> {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(roleId);
                    rp.setPermissionId(request.getPermissionId());
                    return rp;
                })
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(newAssignments);

        List<Long> affectedUserIds = rolePermissionRepository.findUserIdsByRoleIds(request.getRoleIds());
        refreshUserPermissionsCache(Set.copyOf(affectedUserIds));
    }

    @Override
    public void removeRolesFromPermission(AssignRolesToPermissionRequest request) {
        permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Permission not found"));

        List<Long> affectedUserIds = rolePermissionRepository.findUserIdsByRoleIds(request.getRoleIds());

        rolePermissionRepository.deleteByPermissionIdAndRoleIdIn(request.getPermissionId(), request.getRoleIds());

        refreshUserPermissionsCache(Set.copyOf(affectedUserIds));
    }

    @Override
    public void refreshUserPermissionsCache(Set<Long> userIds) {
        userIds.forEach(userId -> {
            List<Permission> permissions = permissionRepository.findPermissionsByUserId(userId);
            Set<PermissionCode> permissionCodes = permissions.stream()
                    .map(permission -> {
                        try {
                            return PermissionCode.valueOf(permission.getPermissionName());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(permissionCode -> permissionCode != null)
                    .collect(Collectors.toSet());

            redisService.saveUserPermissions(userId, permissionCodes);
        });
    }
}
