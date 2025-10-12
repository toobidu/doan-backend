package org.example.quizizz.model.dto.role;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String roleName;
    private String description;
}
