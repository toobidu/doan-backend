package org.example.quizizz.repository;

import org.example.quizizz.model.entity.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByUserId(Long userId);

    // Get recent game history ordered by date
    List<GameHistory> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    // Get highest score
    @Query("SELECT MAX(gh.score) FROM GameHistory gh WHERE gh.userId = :userId")
    Integer findHighestScoreByUserId(@Param("userId") Long userId);

    // Get total games played
    @Query("SELECT COUNT(gh) FROM GameHistory gh WHERE gh.userId = :userId")
    Integer countGamesByUserId(@Param("userId") Long userId);

    // Get total correct answers
    @Query("SELECT SUM(gh.correctAnswers) FROM GameHistory gh WHERE gh.userId = :userId")
    Integer sumCorrectAnswersByUserId(@Param("userId") Long userId);

    // Get average score
    @Query("SELECT AVG(gh.score) FROM GameHistory gh WHERE gh.userId = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);

    // Find all game histories by game session id
    List<GameHistory> findByGameSessionId(Long gameSessionId);
}
