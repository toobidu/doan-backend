package org.example.quizizz.repository;

import org.example.quizizz.model.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByRoomId(Long roomId);

    // ✅ Tìm tất cả answers của một player trong một room
    List<UserAnswer> findByRoomIdAndUserId(Long roomId, Long userId);

    // ✅ Tìm tất cả answers cho một câu hỏi cụ thể trong một room
    List<UserAnswer> findByRoomIdAndQuestionId(Long roomId, Long questionId);
}
