package org.example.quizizz.service.Implement;

import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.common.exception.ApiException;
import org.example.quizizz.mapper.RoleMapper;
import org.example.quizizz.model.dto.permission.PermissionResponse;
import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;
import org.example.quizizz.model.entity.Role;
import org.example.quizizz.model.entity.Permission;
import org.example.quizizz.model.entity.RolePermission;
import org.example.quizizz.repository.RoleRepository;
import org.example.quizizz.repository.PermissionRepository;
import org.example.quizizz.repository.RolePermissionRepository;
import org.example.quizizz.service.Interface.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImplement implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleResponse create(CreateRoleRequest request) {
        String normalizedRoleName = request.getRoleName().trim().toUpperCase();
        if (roleRepository.findByRoleName(normalizedRoleName).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.ROLE_ALREADY_EXISTS, "Role name already exists");
        }
        Role role = roleMapper.toEntity(request);
        role.setRoleName(normalizedRoleName);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleResponse update(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        if (request.getRoleName() != null) {
            String normalizedRoleName = request.getRoleName().trim().toUpperCase();
            if (!normalizedRoleName.equals(role.getRoleName()) &&
                roleRepository.findByRoleName(normalizedRoleName).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.ROLE_ALREADY_EXISTS, "Role name already exists");
            }
            role.setRoleName(normalizedRoleName);
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        
        if ("1".equals(role.getSystemFlag())) {
            throw new ApiException(HttpStatus.FORBIDDEN.value(), MessageCode.OPERATION_NOT_ALLOWED, "Cannot delete system role");
        }
        
        roleRepository.delete(role);
    }

    @Override
    @Cacheable(value = "role", key = "#id")
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        return roleMapper.toResponse(role);
    }

    @Override
    @Cacheable("roles")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public Long count() {
        return roleRepository.count();
    }

    @Override
    public org.springframework.data.domain.Page<RoleResponse> searchRoles(String keyword, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Role> roles;
        if (keyword != null && !keyword.trim().isEmpty()) {
            roles = roleRepository.findByRoleNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
        } else {
            roles = roleRepository.findAll(pageable);
        }
        return roles.map(roleMapper::toResponse);
    }

    @Override
    public List<PermissionResponse> getRolePermissions(Long roleId) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(roleId);
        List<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).toList();
        
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        return permissionRepository.findAllById(permissionIds).stream()
                .map(permission -> {
                    PermissionResponse response = new PermissionResponse();
                    response.setId(permission.getId());
                    response.setPermissionName(permission.getPermissionName());
                    response.setDescription(permission.getDescription());
                    response.setSystemFlag(permission.getSystemFlag());
                    response.setCreatedAt(permission.getCreatedAt());
                    response.setUpdatedAt(permission.getUpdatedAt());
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.ROLE_NOT_FOUND, "Role not found"));
        
        // Clear existing permissions
        rolePermissionRepository.deleteByRoleId(roleId);
        
        // Add new permissions if provided
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                throw new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Some permissions not found");
            }
            
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        RolePermission rp = new RolePermission();
                        rp.setRoleId(roleId);
                        rp.setPermissionId(permissionId);
                        return rp;
                    })
                    .toList();
            
            rolePermissionRepository.saveAll(rolePermissions);
        }
    }
}
