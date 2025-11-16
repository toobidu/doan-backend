package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.question.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IQuestionService {
    List<QuestionWithAnswersResponse> getRandomQuestionsWithAnswers(Long examId, String questionType, int count);
    List<QuestionWithAnswersResponse> getRandomQuestionsForPlayer(Long examId, String questionType, int count, Long playerId);
    long countAvailableQuestions(Long examId, String questionType);

    // CRUD operations
    QuestionResponse createQuestion(CreateQuestionRequest request);
    List<QuestionResponse> createBulkQuestions(CreateBulkQuestionsRequest request);
    QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request);
    void deleteQuestion(Long id);
    void deleteBulkQuestions(DeleteBulkQuestionsRequest request);
    QuestionResponse getQuestionById(Long id);
    List<QuestionWithAnswersResponse> getQuestionsByExamId(Long examId);
    Page<QuestionWithAnswersResponse> search(String keyword, Long examId, String questionType, Pageable pageable);
}
