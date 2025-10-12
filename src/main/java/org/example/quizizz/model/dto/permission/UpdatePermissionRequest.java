package org.example.quizizz.model.dto.permission;

import lombok.Data;

@Data
public class UpdatePermissionRequest {
    private String permissionName;
    private String description;
}
