package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.room.CreateRoomRequest;
import org.example.quizizz.model.dto.room.RoomResponse;
import org.example.quizizz.model.dto.room.UpdateRoomRequest;
import org.example.quizizz.model.entity.Room;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hasGameHistory", ignore = true)
    @Mapping(target = "roomMode", expression = "java(request.getRoomMode().name())")
    @Mapping(target = "status", expression = "java(org.example.quizizz.common.constants.RoomStatus.WAITING.name())")
    Room toEntity(CreateRoomRequest request);

    @Mapping(target = "roomMode", expression = "java(org.example.quizizz.common.constants.RoomMode.valueOf(room.getRoomMode()))")
    @Mapping(target = "status", expression = "java(org.example.quizizz.common.constants.RoomStatus.valueOf(room.getStatus()))")
    @Mapping(target = "topicName", ignore = true)
    @Mapping(target = "ownerUsername", ignore = true)
    @Mapping(target = "currentPlayers", ignore = true)
    RoomResponse toResponse(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "hasGameHistory", ignore = true)
    @Mapping(target = "roomMode", expression = "java(request.getRoomMode() != null ? request.getRoomMode().name() : null)")
    void updateEntityFromRequest(@MappingTarget Room room, UpdateRoomRequest request);
}
