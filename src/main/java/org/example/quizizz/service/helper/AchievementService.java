package org.example.quizizz.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.model.dto.profile.ProfileStatsResponse;
import org.example.quizizz.model.entity.GameHistory;
import org.example.quizizz.model.enums.Achievement;
import org.example.quizizz.repository.GameHistoryRepository;
import org.example.quizizz.repository.GameSessionRepository;
import org.example.quizizz.util.AchievementChecker;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service để tính toán và generate achievements cho user
 * Tách riêng để dễ maintain và test
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementChecker achievementChecker;
    private final GameHistoryRepository gameHistoryRepository;
    private final GameSessionRepository gameSessionRepository;

    /**
     * Generate tất cả achievements cho user
     */
    public List<ProfileStatsResponse.AchievementDto> generateAchievements(
            Long userId,
            List<GameHistory> gameHistories
    ) {
        log.info("Generating achievements for user {}", userId);

        // Tính toán các stats cần thiết
        Map<String, Object> stats = calculateStats(userId, gameHistories);

        // Generate achievements
        List<ProfileStatsResponse.AchievementDto> achievements = new ArrayList<>();

        for (Achievement achievement : Achievement.values()) {
            boolean earned = achievementChecker.checkAchievement(achievement, gameHistories, stats);

            achievements.add(ProfileStatsResponse.AchievementDto.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .tier(achievement.getTier())
                .points(achievement.getPoints())
                .earned(earned)
                .iconUrl(null)
                .build());
        }

        long earnedCount = achievements.stream().filter(ProfileStatsResponse.AchievementDto::getEarned).count();
        log.info("Generated {} achievements for user {}, {} earned", achievements.size(), userId, earnedCount);

        return achievements;
    }

    /**
     * Tính toán các stats cần thiết
     */
    private Map<String, Object> calculateStats(Long userId, List<GameHistory> gameHistories) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalGames", gameHistories.size());

        // First place và top 3 count
        int firstPlaceCount = 0;
        int topThreeCount = 0;

        for (GameHistory gh : gameHistories) {
            try {
                List<GameHistory> sessionHistories = gameHistoryRepository.findByGameSessionId(gh.getGameSessionId());
                sessionHistories.sort((a, b) -> b.getScore().compareTo(a.getScore()));

                int ranking = 1;
                for (GameHistory history : sessionHistories) {
                    if (history.getId().equals(gh.getId())) {
                        if (ranking == 1) firstPlaceCount++;
                        if (ranking <= 3) topThreeCount++;
                        break;
                    }
                    ranking++;
                }
            } catch (Exception e) {
                log.warn("Error calculating ranking: {}", e.getMessage());
            }
        }

        stats.put("firstPlaceCount", firstPlaceCount);
        stats.put("topThreeCount", topThreeCount);

        // Fastest time
        Long fastestTime = null;
        for (GameHistory gh : gameHistories) {
            try {
                var gameSession = gameSessionRepository.findById(gh.getGameSessionId()).orElse(null);
                if (gameSession != null && gameSession.getStartTime() != null && gameSession.getEndTime() != null) {
                    long duration = java.time.Duration.between(gameSession.getStartTime(), gameSession.getEndTime()).getSeconds();
                    if (fastestTime == null || duration < fastestTime) {
                        fastestTime = duration;
                    }
                }
            } catch (Exception e) {
                log.warn("Error calculating fastest time: {}", e.getMessage());
            }
        }
        stats.put("fastestTime", fastestTime);

        // TODO: Implement các stats phức tạp hơn
        stats.put("maxWinStreak", 0);
        stats.put("consecutiveDays", 0);
        stats.put("uniqueTopicsPlayed", 0);
        stats.put("masteredTopicsCount", 0);
        stats.put("bestTopicAccuracy", 0.0);

        return stats;
    }

    public int getTotalAchievementPoints(List<ProfileStatsResponse.AchievementDto> achievements) {
        return achievements.stream()
            .filter(ProfileStatsResponse.AchievementDto::getEarned)
            .mapToInt(ProfileStatsResponse.AchievementDto::getPoints)
            .sum();
    }

    public double getAchievementProgress(List<ProfileStatsResponse.AchievementDto> achievements) {
        long earned = achievements.stream().filter(ProfileStatsResponse.AchievementDto::getEarned).count();
        return achievements.isEmpty() ? 0.0 : (earned * 100.0 / achievements.size());
    }
}


