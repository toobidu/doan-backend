package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.topic.CreateTopicRequest;
import org.example.quizizz.model.dto.topic.TopicResponse;
import org.example.quizizz.model.dto.topic.UpdateTopicRequest;
import org.example.quizizz.model.entity.Topic;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Topic toTopic(CreateTopicRequest request);

    TopicResponse toTopicResponse(Topic topic);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateTopicFromRequest(@MappingTarget Topic topic, UpdateTopicRequest request);
}
