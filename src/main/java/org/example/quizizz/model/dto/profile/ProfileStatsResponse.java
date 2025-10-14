package org.example.quizizz.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStatsResponse {
    private Integer gamesPlayed;
    private Integer highestScore;
    private Integer highestRank;
    private String fastestTime;
    private String bestTopic;
    private Integer totalScore;
    private Double averageScore;
    private Integer medals;
    private Integer currentRank;
    private List<AchievementDto> achievements;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementDto {
        private String name;
        private String description;
        private Boolean earned;
        private String iconUrl;
    }
}

