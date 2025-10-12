package org.example.quizizz.mapper;

import org.example.quizizz.model.dto.question.CreateQuestionRequest;
import org.example.quizizz.model.dto.question.QuestionResponse;
import org.example.quizizz.model.dto.question.UpdateQuestionRequest;
import org.example.quizizz.model.entity.Question;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    /**
     * Map từ CreateQuestionRequest sang Question entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questionType", expression = "java(request.getQuestionType() != null ? request.getQuestionType().toUpperCase() : null)")
    Question toEntity(CreateQuestionRequest request);

    /**
     * Map từ Question entity sang QuestionResponse
     */
    QuestionResponse toResponse(Question question);

    /**
     * Map từ UpdateQuestionRequest sang Question entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questionType", expression = "java(request.getQuestionType() != null ? request.getQuestionType().toUpperCase() : null)")
    void updateEntityFromRequest(@MappingTarget Question question, UpdateQuestionRequest request);
}
