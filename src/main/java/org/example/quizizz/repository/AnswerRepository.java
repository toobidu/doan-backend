package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);
    
    org.springframework.data.domain.Page<Answer> findByAnswerTextContainingIgnoreCase(String keyword, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Answer> findByQuestionIdAndAnswerTextContainingIgnoreCase(Long questionId, String keyword, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Answer> findByIsCorrectAndAnswerTextContainingIgnoreCase(Boolean isCorrect, String keyword, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Answer> findByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Answer> findByQuestionIdAndIsCorrectAndAnswerTextContainingIgnoreCase(Long questionId, Boolean isCorrect, String keyword, org.springframework.data.domain.Pageable pageable);
}
