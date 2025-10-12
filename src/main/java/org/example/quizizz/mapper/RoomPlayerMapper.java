package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.room.RoomPlayerResponse;
import org.example.quizizz.model.entity.RoomPlayers;
import org.example.quizizz.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomPlayerMapper {

    @Mapping(target = "id", source = "roomPlayer.id")
    @Mapping(target = "userId", source = "roomPlayer.userId")
    @Mapping(target = "isHost", source = "roomPlayer.isHost")
    @Mapping(target = "joinedAt", source = "roomPlayer.createdAt")
    @Mapping(target = "username", source = "user.username")
    RoomPlayerResponse toResponse(RoomPlayers roomPlayer, User user);
    
    @Mapping(target = "joinedAt", source = "createdAt")
    @Mapping(target = "username", ignore = true)
    RoomPlayerResponse toResponse(RoomPlayers roomPlayer);
}
