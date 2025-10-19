package org.example.quizizz.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsResponse {
    private Integer highestScore;
    private Integer highestRanking;
    private Long fastestTime;
    private String bestTopic;
    private Integer totalGamesPlayed;
    private Integer totalCorrectAnswers;
    private Double averageScore;
    private Double accuracyRate;
}
