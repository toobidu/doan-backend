package org.example.quizizz.model.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;
}
