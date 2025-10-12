package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.profile.UpdateProfileRequest;
import org.example.quizizz.model.dto.profile.UpdateProfileResponse;
import org.example.quizizz.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "deleted", ignore = true)
    void updateUserFromDto(UpdateProfileRequest dto, @MappingTarget User user);

    UpdateProfileResponse toResponse(User user);
}
