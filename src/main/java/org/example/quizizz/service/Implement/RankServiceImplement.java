package org.example.quizizz.service.Implement;

import org.example.quizizz.model.entity.Rank;
import org.example.quizizz.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RankServiceImplement {
    
    private final RankRepository rankRepository;
    
    public void updateRankAfterGame(Long userId, int scoreEarned, long timeTaken) {
        Rank rank = rankRepository.findByUserId(userId)
            .orElseGet(() -> {
                Rank newRank = new Rank();
                newRank.setUserId(userId);
                newRank.setTotalScore(0);
                newRank.setGamePlayed(0);
                newRank.setTotalTime(0L);
                return newRank;
            });
        
        rank.setTotalScore(rank.getTotalScore() + scoreEarned);
        rank.setGamePlayed(rank.getGamePlayed() + 1);
        rank.setTotalTime(rank.getTotalTime() + timeTaken);
        rankRepository.save(rank);
    }

    // Giữ lại method cũ để backward compatibility
    public void updateRankAfterGame(Long userId, int scoreEarned) {
        updateRankAfterGame(userId, scoreEarned, 0L);
    }
}
