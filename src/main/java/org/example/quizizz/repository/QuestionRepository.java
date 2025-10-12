package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsByQuestionText(String questionText);
    List<Question> findQuestionByTopicId(Long topicId);
    
    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId AND question_type = :questionType ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByTopicAndType(@Param("topicId") Long topicId, @Param("questionType") String questionType, @Param("limit") int limit);
    
    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByTopic(@Param("topicId") Long topicId, @Param("limit") int limit);
    
    @Query(value = "SELECT * FROM questions WHERE question_type = :questionType ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByType(@Param("questionType") String questionType, @Param("limit") int limit);
    
    long countByTopicIdAndQuestionType(Long topicId, String questionType);
    long countByTopicId(Long topicId);
}
