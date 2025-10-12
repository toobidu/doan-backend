package org.example.quizizz.repository;

import org.example.quizizz.model.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByRoomId(Long roomId);
    List<UserAnswer> findByRoomIdAndUserId(Long roomId, Long userId);
    List<UserAnswer> findByRoomIdAndQuestionId(Long roomId, Long questionId);
    List<UserAnswer> findByUserId(Long userId);
}
