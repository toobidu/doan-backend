package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.question.*;

import java.util.List;

public interface IQuestionService {
    List<QuestionWithAnswersResponse> getRandomQuestionsWithAnswers(Long topicId, String questionType, int count);
    List<QuestionWithAnswersResponse> getRandomQuestionsForPlayer(Long topicId, String questionType, int count, Long playerId);
    long countAvailableQuestions(Long topicId, String questionType);

    // CRUD operations
    QuestionResponse createQuestion(CreateQuestionRequest request);
    List<QuestionResponse> createBulkQuestions(CreateBulkQuestionsRequest request);
    QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request);
    void deleteQuestion(Long id);
    void deleteBulkQuestions(DeleteBulkQuestionsRequest request);
    QuestionResponse getQuestionById(Long id);
    List<QuestionWithAnswersResponse> getQuestionsByTopicId(Long topicId);
}
