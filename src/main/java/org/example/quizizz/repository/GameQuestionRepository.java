package org.example.quizizz.repository;

import org.example.quizizz.model.entity.GameQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {
    List<GameQuestion> findByGameSessionIdOrderByQuestionOrder(Long gameSessionId);
}
