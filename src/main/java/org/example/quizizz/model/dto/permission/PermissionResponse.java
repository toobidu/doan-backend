package org.example.quizizz.model.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PermissionResponse {
    private Long id;
    private String permissionName;
    private String description;
}
