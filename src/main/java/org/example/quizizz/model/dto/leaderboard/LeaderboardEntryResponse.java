package org.example.quizizz.model.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private Integer rank;
    private Long userId;
    private String username;
    private String avatarURL;
    private Double averageScore;
    private Integer gamesPlayed;
}
