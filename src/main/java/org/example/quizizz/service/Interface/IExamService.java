package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.exam.CreateExamRequest;
import org.example.quizizz.model.dto.exam.ExamResponse;
import org.example.quizizz.model.dto.exam.UpdateExamRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IExamService {
    ExamResponse create(CreateExamRequest request, Long teacherId);
    ExamResponse update(Long id, UpdateExamRequest request);
    void delete(Long id);
    ExamResponse getById(Long id);
    List<ExamResponse> getAll();
    List<ExamResponse> getByTopicId(Long topicId);
    Page<ExamResponse> search(String keyword, Long topicId, Pageable pageable);
    long count();
}
