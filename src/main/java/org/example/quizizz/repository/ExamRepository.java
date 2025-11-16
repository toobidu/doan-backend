package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByTopicId(Long topicId);
    Page<Exam> findByTopicId(Long topicId, Pageable pageable);
    Page<Exam> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Exam> findByTopicIdAndTitleContainingIgnoreCase(Long topicId, String keyword, Pageable pageable);
    void deleteByTopicId(Long topicId);
}
