package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.leaderboard.LeaderboardEntryResponse;

import java.util.List;

public interface ILeaderboardService {
    List<LeaderboardEntryResponse> getLeaderboardByTopic(Long topicId);
}
