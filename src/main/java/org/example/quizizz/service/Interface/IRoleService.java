package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;

import java.util.List;

public interface IRoleService {
    RoleResponse create(CreateRoleRequest request);
    RoleResponse update(Long id, UpdateRoleRequest request);
    void delete(Long id);
    RoleResponse getById(Long id);
    List<RoleResponse> getAll();
}
