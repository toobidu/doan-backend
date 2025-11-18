package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.permission.CreatePermissionRequest;
import org.example.quizizz.model.dto.permission.PermissionResponse;
import org.example.quizizz.model.dto.permission.UpdatePermissionRequest;
import org.example.quizizz.model.entity.Permission;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "permissionName", source = "permissionName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "systemFlag", constant = "1")
    Permission toEntity(CreatePermissionRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "permissionName", source = "permissionName")
    @Mapping(target = "description", source = "description")
    void updateEntityFromDto(UpdatePermissionRequest dto, @MappingTarget Permission entity);

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PermissionResponse toResponse(Permission entity);
}
