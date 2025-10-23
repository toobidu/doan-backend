package org.example.quizizz.service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.model.dto.leaderboard.LeaderboardEntryResponse;
import org.example.quizizz.model.entity.User;
import org.example.quizizz.repository.GameHistoryRepository;
import org.example.quizizz.repository.UserRepository;
import org.example.quizizz.service.Interface.ILeaderboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardServiceImplement implements ILeaderboardService {

    private final GameHistoryRepository gameHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<LeaderboardEntryResponse> getLeaderboardByTopic(Long topicId) {
        List<Object[]> results = gameHistoryRepository.findLeaderboardByTopicId(topicId);
        List<LeaderboardEntryResponse> leaderboard = new ArrayList<>();
        
        int rank = 1;
        for (Object[] result : results) {
            if (rank > 20) break;
            
            Long userId = ((Number) result[0]).longValue();
            Double avgScore = ((Number) result[1]).doubleValue();
            Integer gamesPlayed = ((Number) result[2]).intValue();
            
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                leaderboard.add(LeaderboardEntryResponse.builder()
                        .rank(rank++)
                        .userId(userId)
                        .username(user.getUsername())
                        .avatarURL(user.getAvatarURL())
                        .averageScore(Math.round(avgScore * 100.0) / 100.0)
                        .gamesPlayed(gamesPlayed)
                        .build());
            }
        }
        
        return leaderboard;
    }
}
