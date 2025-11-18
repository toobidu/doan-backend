package org.example.quizizz.model.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private Long id;
    private String permissionName;
    private String description;
    private String systemFlag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
