package org.example.quizizz.util;

import org.example.quizizz.model.entity.GameHistory;
import org.example.quizizz.model.enums.Achievement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Utility class để kiểm tra điều kiện đạt achievement
 * Tách riêng logic để dễ test và maintain
 */
@Component
public class AchievementChecker {

    /**
     * Kiểm tra achievement có được đạt hay không
     */
    public boolean checkAchievement(
            Achievement achievement,
            List<GameHistory> gameHistories,
            Map<String, Object> stats
    ) {
        return switch (achievement.getType()) {
            case GAMES_PLAYED -> checkGamesPlayed(achievement, gameHistories);
            case TOTAL_SCORE -> checkTotalScore(achievement, gameHistories);
            case CORRECT_ANSWERS -> checkCorrectAnswers(achievement, gameHistories);
            case ACCURACY -> checkAccuracy(achievement, stats);
            case RANKING -> checkRanking(achievement, stats);
            case PERFECT_GAME -> checkPerfectGame(achievement, gameHistories);
            case SPEED -> checkSpeed(achievement, stats);
            case STREAK -> checkStreak(achievement, stats);
            case WIN_STREAK -> checkWinStreak(achievement, stats);
            case TOPIC_VARIETY -> checkTopicVariety(achievement, stats);
            case MASTERY -> checkMastery(achievement, stats);
        };
    }

    private boolean checkGamesPlayed(Achievement achievement, List<GameHistory> gameHistories) {
        int totalGames = gameHistories.size();
        return switch (achievement.getId()) {
            case "beginner" -> totalGames >= 1;
            case "enthusiast" -> totalGames >= 10;
            case "legend" -> totalGames >= 50;
            case "immortal" -> totalGames >= 100;
            default -> false;
        };
    }

    private boolean checkTotalScore(Achievement achievement, List<GameHistory> gameHistories) {
        int totalScore = gameHistories.stream()
                .mapToInt(gh -> gh.getScore() != null ? gh.getScore() : 0)
                .sum();

        return switch (achievement.getId()) {
            case "score_hunter" -> totalScore >= 500;
            case "expert" -> totalScore >= 1000;
            case "quiz_master" -> totalScore >= 2000;
            default -> false;
        };
    }

    private boolean checkCorrectAnswers(Achievement achievement, List<GameHistory> gameHistories) {
        int totalCorrect = gameHistories.stream()
                .mapToInt(gh -> gh.getCorrectAnswers() != null ? gh.getCorrectAnswers() : 0)
                .sum();

        return switch (achievement.getId()) {
            case "quick_learner" -> totalCorrect >= 10;
            case "sharp_shooter" -> totalCorrect >= 100;
            case "answer_machine" -> totalCorrect >= 500;
            default -> false;
        };
    }

    private boolean checkAccuracy(Achievement achievement, Map<String, Object> stats) {
        if ("topic_master".equals(achievement.getId())) {
            Double bestTopicAccuracy = (Double) stats.get("bestTopicAccuracy");
            return bestTopicAccuracy != null && bestTopicAccuracy >= 90.0;
        }
        return false;
    }

    private boolean checkRanking(Achievement achievement, Map<String, Object> stats) {
        Integer firstPlaceCount = (Integer) stats.get("firstPlaceCount");
        Integer topThreeCount = (Integer) stats.get("topThreeCount");

        if (firstPlaceCount == null) firstPlaceCount = 0;
        if (topThreeCount == null) topThreeCount = 0;

        return switch (achievement.getId()) {
            case "first_win" -> firstPlaceCount >= 1;
            case "champion" -> firstPlaceCount >= 5;
            case "grandmaster" -> firstPlaceCount >= 20;
            case "top_three" -> topThreeCount >= 10;
            default -> false;
        };
    }

    private boolean checkPerfectGame(Achievement achievement, List<GameHistory> gameHistories) {
        if ("perfectionist".equals(achievement.getId())) {
            return gameHistories.stream()
                    .anyMatch(gh ->
                            gh.getTotalQuestions() != null
                                    && gh.getTotalQuestions() > 0
                                    && gh.getCorrectAnswers() != null
                                    && gh.getCorrectAnswers().equals(gh.getTotalQuestions())
                    );
        }
        return false;
    }

    private boolean checkSpeed(Achievement achievement, Map<String, Object> stats) {
        if ("speed_demon".equals(achievement.getId())) {
            Long fastestTime = (Long) stats.get("fastestTime");
            return fastestTime != null && fastestTime > 0 && fastestTime <= 120;
        }
        return false;
    }

    private boolean checkStreak(Achievement achievement, Map<String, Object> stats) {
        if ("consistent_player".equals(achievement.getId())) {
            Integer consecutiveDays = (Integer) stats.get("consecutiveDays");
            return consecutiveDays != null && consecutiveDays >= 5;
        }
        return false;
    }

    private boolean checkWinStreak(Achievement achievement, Map<String, Object> stats) {
        if ("unstoppable".equals(achievement.getId())) {
            Integer maxWinStreak = (Integer) stats.get("maxWinStreak");
            return maxWinStreak != null && maxWinStreak >= 3;
        }
        return false;
    }

    private boolean checkTopicVariety(Achievement achievement, Map<String, Object> stats) {
        if ("knowledge_seeker".equals(achievement.getId())) {
            Integer uniqueTopicsPlayed = (Integer) stats.get("uniqueTopicsPlayed");
            return uniqueTopicsPlayed != null && uniqueTopicsPlayed >= 5;
        }
        return false;
    }

    private boolean checkMastery(Achievement achievement, Map<String, Object> stats) {
        if ("master_of_all".equals(achievement.getId())) {
            Integer masteredTopicsCount = (Integer) stats.get("masteredTopicsCount");
            return masteredTopicsCount != null && masteredTopicsCount >= 5;
        }
        return false;
    }
}