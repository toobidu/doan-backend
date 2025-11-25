package org.example.quizizz.common.constants;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * Enum định nghĩa tất cả các thành tựu (achievements) trong hệ thống
 * Dễ dàng mở rộng bằng cách thêm enum constant mới
 */
@Getter
@AllArgsConstructor
public enum Achievement {

    // ===== BRONZE TIER =====
    BEGINNER(
        "beginner",
        "🎯 Người Mới Bắt Đầu",
        "Hoàn thành trò chơi đầu tiên",
        "bronze",
        10,
        AchievementType.GAMES_PLAYED
    ),

    FIRST_WIN(
        "first_win",
        "🏆 Chiến Thắng Đầu Tiên",
        "Đạt hạng 1 trong game đầu tiên",
        "bronze",
        20,
        AchievementType.RANKING
    ),

    QUICK_LEARNER(
        "quick_learner",
        "📚 Học Nhanh",
        "Trả lời đúng 10 câu hỏi",
        "bronze",
        15,
        AchievementType.CORRECT_ANSWERS
    ),

    // ===== SILVER TIER =====
    ENTHUSIAST(
        "enthusiast",
        "🔥 Người Chơi Nhiệt Tình",
        "Chơi 10 trò chơi",
        "silver",
        50,
        AchievementType.GAMES_PLAYED
    ),

    TOPIC_MASTER(
        "topic_master",
        "📖 Bậc Thầy Chủ Đề",
        "Đạt trên 90% chính xác ở 1 chủ đề",
        "silver",
        80,
        AchievementType.ACCURACY
    ),

    CONSISTENT_PLAYER(
        "consistent_player",
        "📈 Nhất Quán",
        "Chơi 5 ngày liên tiếp",
        "silver",
        100,
        AchievementType.STREAK
    ),

    SHARP_SHOOTER(
        "sharp_shooter",
        "🎯 Xạ Thủ",
        "Trả lời đúng 100 câu",
        "silver",
        120,
        AchievementType.CORRECT_ANSWERS
    ),

    SCORE_HUNTER(
        "score_hunter",
        "💯 Thợ Săn Điểm",
        "Đạt 500 điểm tổng",
        "silver",
        75,
        AchievementType.TOTAL_SCORE
    ),

    // ===== GOLD TIER =====
    EXPERT(
        "expert",
        "💎 Cao Thủ",
        "Đạt 1000 điểm tổng",
        "gold",
        100,
        AchievementType.TOTAL_SCORE
    ),

    PERFECTIONIST(
        "perfectionist",
        "🎖️ Hoàn Hảo",
        "Đạt 100% chính xác trong 1 game",
        "gold",
        150,
        AchievementType.PERFECT_GAME
    ),

    SPEED_DEMON(
        "speed_demon",
        "⚡ Tốc Độ Ánh Sáng",
        "Hoàn thành game dưới 2 phút",
        "gold",
        75,
        AchievementType.SPEED
    ),

    TOP_THREE(
        "top_three",
        "🥉 Top 3",
        "Đạt top 3 trong 10 game",
        "gold",
        125,
        AchievementType.RANKING
    ),

    KNOWLEDGE_SEEKER(
        "knowledge_seeker",
        "🧠 Người Tìm Kiến Thức",
        "Chơi qua 5 chủ đề khác nhau",
        "gold",
        90,
        AchievementType.TOPIC_VARIETY
    ),

    // ===== PLATINUM TIER =====
    LEGEND(
        "legend",
        "👑 Huyền Thoại",
        "Chơi 50 trò chơi",
        "platinum",
        200,
        AchievementType.GAMES_PLAYED
    ),

    CHAMPION(
        "champion",
        "🏆 Nhà Vô Địch",
        "Đạt hạng 1 trong 5 game",
        "platinum",
        250,
        AchievementType.RANKING
    ),

    MASTER_OF_ALL(
        "master_of_all",
        "🌟 Bậc Thầy Toàn Năng",
        "Đạt 80% chính xác ở 5 chủ đề khác nhau",
        "platinum",
        300,
        AchievementType.MASTERY
    ),

    UNSTOPPABLE(
        "unstoppable",
        "🔥 Không Thể Cản",
        "Giành chiến thắng 3 game liên tiếp",
        "platinum",
        275,
        AchievementType.WIN_STREAK
    ),

    QUIZ_MASTER(
        "quiz_master",
        "🎓 Bậc Thầy Quiz",
        "Đạt 2000 điểm tổng",
        "platinum",
        350,
        AchievementType.TOTAL_SCORE
    ),

    ANSWER_MACHINE(
        "answer_machine",
        "🤖 Cỗ Máy Trả Lời",
        "Trả lời đúng 500 câu",
        "platinum",
        400,
        AchievementType.CORRECT_ANSWERS
    ),

    // ===== DIAMOND TIER =====
    IMMORTAL(
        "immortal",
        "💠 Bất Tử",
        "Chơi 100 trò chơi",
        "diamond",
        500,
        AchievementType.GAMES_PLAYED
    ),

    GRANDMASTER(
        "grandmaster",
        "👑 Đại Cao Thủ",
        "Đạt hạng 1 trong 20 game",
        "diamond",
        750,
        AchievementType.RANKING
    );

    private final String id;
    private final String name;
    private final String description;
    private final String tier;
    private final Integer points;
    private final AchievementType type;

    /**
     * Enum để phân loại các loại achievement
     */
    public enum AchievementType {
        GAMES_PLAYED,
        TOTAL_SCORE,
        CORRECT_ANSWERS,
        ACCURACY,
        RANKING,
        PERFECT_GAME,
        SPEED,
        STREAK,
        WIN_STREAK,
        TOPIC_VARIETY,
        MASTERY
    }

    /**
     * Lấy tất cả achievements theo tier
     */
    public static Achievement[] getByTier(String tier) {
        return java.util.Arrays.stream(values())
            .filter(a -> a.getTier().equalsIgnoreCase(tier))
            .toArray(Achievement[]::new);
    }

    /**
     * Lấy achievement theo ID
     */
    public static Achievement getById(String id) {
        return java.util.Arrays.stream(values())
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}

