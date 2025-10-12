package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.answer.AnswerResponse;
import org.example.quizizz.model.dto.answer.CreateAnswerRequest;
import org.example.quizizz.model.dto.answer.UpdateAnswerRequest;
import org.example.quizizz.model.entity.Answer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    @Mapping(target = "id", ignore = true)
    Answer toEntity(CreateAnswerRequest request);

    AnswerResponse toResponse(Answer answer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "questionId", ignore = true)
    void updateEntityFromRequest(@MappingTarget Answer answer, UpdateAnswerRequest request);
}
