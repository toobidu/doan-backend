package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.answer.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAnswerService {
    AnswerResponse createAnswer(CreateAnswerRequest request);
    List<AnswerResponse> createBulkAnswers(CreateBulkAnswersRequest request);
    AnswerResponse updateAnswer(Long id, UpdateAnswerRequest request);
    void deleteAnswer(Long id);
    AnswerResponse getAnswerById(Long id);
    Page<AnswerResponse> search(String keyword, Long questionId, Boolean isCorrect, Pageable pageable);
}
