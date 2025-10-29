package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);
    
    Page<Answer> findByAnswerTextContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Answer> findByQuestionIdAndAnswerTextContainingIgnoreCase(Long questionId, String keyword, Pageable pageable);
    Page<Answer> findByIsCorrectAndAnswerTextContainingIgnoreCase(Boolean isCorrect, String keyword, Pageable pageable);
    Page<Answer> findByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect, Pageable pageable);
    Page<Answer> findByQuestionIdAndIsCorrectAndAnswerTextContainingIgnoreCase(Long questionId, Boolean isCorrect, String keyword, Pageable pageable);
}
