package org.example.quizizz.model.dto.permission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class AssignRolesToPermissionRequest {
    @NotEmpty(message = "Role IDs cannot be empty")
    private Set<Long> roleIds;

    @NotNull(message = "Permission ID is required")
    private Long permissionId;
}
