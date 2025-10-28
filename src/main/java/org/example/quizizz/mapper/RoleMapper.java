package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.role.CreateRoleRequest;
import org.example.quizizz.model.dto.role.RoleResponse;
import org.example.quizizz.model.dto.role.UpdateRoleRequest;
import org.example.quizizz.model.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "roleName", expression = "java(request.getRoleName().trim().toUpperCase())")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "systemFlag", constant = "1")
    Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    @Mapping(target = "roleName", expression = "java(request.getRoleName() != null ? request.getRoleName().trim().toUpperCase() : role.getRoleName())")
    @Mapping(target = "description", source = "description")
    void updateEntityFromDto(UpdateRoleRequest request, @MappingTarget Role role);
}
