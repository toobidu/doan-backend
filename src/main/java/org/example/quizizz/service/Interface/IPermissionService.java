package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.permission.CreatePermissionRequest;
import org.example.quizizz.model.dto.permission.PermissionResponse;
import org.example.quizizz.model.dto.permission.UpdatePermissionRequest;

import java.util.List;

public interface IPermissionService {
    PermissionResponse create(CreatePermissionRequest request);
    PermissionResponse update(Long id, UpdatePermissionRequest request);
    void delete(Long id);
    PermissionResponse getById(Long id);
    List<PermissionResponse> getAll();
    Long count();
    org.springframework.data.domain.Page<PermissionResponse> searchPermissions(String keyword, org.springframework.data.domain.Pageable pageable);
}
