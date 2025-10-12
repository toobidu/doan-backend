package org.example.quizizz.service.Implement;

import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.common.exception.ApiException;
import org.example.quizizz.mapper.PermissionMapper;
import org.example.quizizz.model.dto.permission.CreatePermissionRequest;
import org.example.quizizz.model.dto.permission.PermissionResponse;
import org.example.quizizz.model.dto.permission.UpdatePermissionRequest;
import org.example.quizizz.model.entity.Permission;
import org.example.quizizz.repository.PermissionRepository;
import org.example.quizizz.service.Interface.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quản lý quyền (permission) của hệ thống.
 * Tạo, cập nhật, xóa, lấy quyền và cache kết quả để tối ưu hiệu năng.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImplement implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Tạo mới quyền trong hệ thống.
     * @param request Thông tin quyền cần tạo
     * @return Thông tin quyền vừa tạo
     */
    @Override
    @CacheEvict(value = {"permissions", "permission"}, allEntries = true)
    public PermissionResponse create(CreatePermissionRequest request) {
        String normalizedPermissionName = request.getPermissionName().trim().toUpperCase();
        if (permissionRepository.findByPermissionName(normalizedPermissionName).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.PERMISSION_ALREADY_EXISTS, "Permission name already exists");
        }
        Permission permission = permissionMapper.toEntity(request);
        permission.setPermissionName(normalizedPermissionName);
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    /**
     * Cập nhật thông tin quyền.
     * @param id Id quyền
     * @param request Thông tin cập nhật
     * @return Thông tin quyền sau cập nhật
     */
    @Override
    @CacheEvict(value = {"permissions", "permission"}, allEntries = true)
    public PermissionResponse update(Long id, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Permission not found"));
        if (request.getPermissionName() != null &&
                !request.getPermissionName().trim().equalsIgnoreCase(permission.getPermissionName())) {
            String normalizedPermissionName = request.getPermissionName().trim().toUpperCase();
            if (permissionRepository.findByPermissionName(normalizedPermissionName).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.PERMISSION_ALREADY_EXISTS, "Permission name already exists");
            }
            permission.setPermissionName(normalizedPermissionName);
        }
        permissionMapper.updateEntityFromDto(request, permission);
        return permissionMapper.toResponse(permissionRepository.save(permission));
    }

    /**
     * Xóa quyền khỏi hệ thống.
     * @param id Id quyền
     */
    @Override
    @CacheEvict(value = {"permissions", "permission"}, allEntries = true)
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Permission not found"));
        permissionRepository.delete(permission);
    }

    /**
     * Lấy thông tin quyền theo id (có cache).
     * @param id Id quyền
     * @return Thông tin quyền
     */
    @Override
    @Cacheable(value = "permission", key = "#id")
    public PermissionResponse getById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.PERMISSION_NOT_FOUND, "Permission not found"));
        return permissionMapper.toResponse(permission);
    }

    /**
     * Lấy danh sách tất cả quyền (có cache).
     * @return Danh sách quyền
     */
    @Override
    @Cacheable("permissions")
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .toList();
    }
}
