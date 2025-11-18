package org.example.quizizz.model.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePermissionRequest {
    @NotBlank(message = "Permission name is required")
    private String permissionName;

    @NotBlank(message = "Description is required")
    private String description;
    
    private String systemFlag = "0";
}
