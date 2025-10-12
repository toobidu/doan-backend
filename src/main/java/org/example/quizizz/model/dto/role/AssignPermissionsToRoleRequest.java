package org.example.quizizz.model.dto.role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class AssignPermissionsToRoleRequest {
    @NotEmpty(message = "Permission IDs cannot be empty")
    private Set<Long> permissionIds;

    @NotNull(message = "Role ID is required")
    private Long roleId;
}
