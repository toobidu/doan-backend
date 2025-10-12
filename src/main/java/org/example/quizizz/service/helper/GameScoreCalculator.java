package org.example.quizizz.service.helper;

import org.springframework.stereotype.Component;

@Component
public class GameScoreCalculator {

    private static final int BASE_SCORE = 10;
    private static final int TIME_BONUS_MAX = 5;

    /**
     * Tính điểm dựa trên độ chính xác và thời gian trả lời
     *
     * @param isCorrect Câu trả lời có đúng không
     * @param timeTaken Thời gian trả lời (milliseconds)
     * @param timeLimit Thời gian giới hạn (seconds)
     * @return Điểm số
     */
    public int calculateScore(boolean isCorrect, long timeTaken, int timeLimit) {
        if (!isCorrect) {
            return 0;
        }

        // Điểm cơ bản cho câu trả lời đúng
        int score = BASE_SCORE;

        // Bonus cho tốc độ trả lời (trả lời nhanh được điểm cao hơn)
        long timeLimitMs = timeLimit * 1000L;
        if (timeTaken < timeLimitMs) {
            double timeRatio = (double) timeTaken / timeLimitMs;
            int timeBonus = (int) (TIME_BONUS_MAX * (1 - timeRatio));
            score += timeBonus;
        }

        return score;
    }

    /**
     * Tính điểm streak (nhiều câu đúng liên tiếp)
     *
     * @param correctStreak Số câu đúng liên tiếp
     * @param baseScore     Điểm cơ bản
     * @return Điểm sau khi áp dụng streak
     */
    public int applyStreakMultiplier(int correctStreak, int baseScore) {
        if (correctStreak < 3) {
            return baseScore;
        }

        // Streak bonus: 3+ correct = 1.2x, 5+ correct = 1.5x, 10+ correct = 2x
        double multiplier = 1.0;
        if (correctStreak >= 10) {
            multiplier = 2.0;
        } else if (correctStreak >= 5) {
            multiplier = 1.5;
        } else if (correctStreak >= 3) {
            multiplier = 1.2;
        }

        return (int) (baseScore * multiplier);
    }
}
