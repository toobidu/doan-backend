package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.exam.CreateExamRequest;
import org.example.quizizz.model.dto.exam.ExamResponse;
import org.example.quizizz.model.dto.exam.UpdateExamRequest;
import org.example.quizizz.model.entity.Exam;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExamMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exam toEntity(CreateExamRequest request);

    @Mapping(target = "topicName", ignore = true)
    @Mapping(target = "questionCount", ignore = true)
    ExamResponse toResponse(Exam exam);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topicId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(@MappingTarget Exam exam, UpdateExamRequest request);
}
