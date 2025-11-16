package org.example.quizizz.service.Implement;

import org.example.quizizz.model.entity.*;
import org.example.quizizz.repository.*;
import org.example.quizizz.service.Interface.IPlayerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlayerProfileServiceImplement implements IPlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;
    private final GameHistoryRepository gameHistoryRepository;
    private final RankRepository rankRepository;

    /**
     * Cập nhật player profile sau khi hoàn thành game
     * Tự động tính toán: average_score, preferredTopics, total_play_time
     */
    @Override
    public void updateProfileAfterGame(Long userId, Long roomId) {
        try {
            log.info("Updating player profile for user {} after game in room {}", userId, roomId);

            // Lấy hoặc tạo mới player profile
            PlayerProfile profile = playerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PlayerProfile newProfile = new PlayerProfile();
                    newProfile.setUserId(userId);
                    newProfile.setAge(18);
                    newProfile.setAverageScore(0.0);
                    newProfile.setTotalPlayTime(0);
                    log.info("Created new player profile for user {}", userId);
                    return newProfile;
                });

            // 1. Tính average score từ game histories
            List<GameHistory> histories = gameHistoryRepository.findByUserId(userId);
            if (!histories.isEmpty()) {
                double avgScore = histories.stream()
                    .mapToInt(GameHistory::getScore)
                    .average()
                    .orElse(0.0);
                profile.setAverageScore(avgScore);
                log.info("Updated average score: {}", avgScore);
            }

            // 2. Cập nhật total play time từ ranks table
            rankRepository.findByUserId(userId).ifPresent(rank -> {
                // Convert từ milliseconds sang seconds
                int totalPlayTimeSeconds = (int) (rank.getTotalTime() / 1000);
                profile.setTotalPlayTime(totalPlayTimeSeconds);
                log.info("Updated total play time: {}s", totalPlayTimeSeconds);
            });

            // Lưu profile
            playerProfileRepository.save(profile);
            log.info("Successfully updated player profile for user {}", userId);

        } catch (Exception e) {
            log.error("Error updating player profile for user {}: {}", userId, e.getMessage(), e);
        }
    }


    /**
     * Khởi tạo player profile cho user mới
     */
    @Override
    public void initializeProfile(Long userId, Integer age) {
        if (playerProfileRepository.findByUserId(userId).isPresent()) {
            log.info("Profile already exists for user {}", userId);
            return;
        }

        PlayerProfile profile = new PlayerProfile();
        profile.setUserId(userId);
        profile.setAge(age != null ? age : 18);
        profile.setAverageScore(0.0);
        profile.setTotalPlayTime(0);

        playerProfileRepository.save(profile);
        log.info("Initialized new player profile for user {}", userId);
    }
}

