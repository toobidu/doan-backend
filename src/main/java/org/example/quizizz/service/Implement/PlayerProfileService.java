package org.example.quizizz.service.Implement;

import org.example.quizizz.model.entity.*;
import org.example.quizizz.repository.*;
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
public class PlayerProfileService {

    private final PlayerProfileRepository playerProfileRepository;
    private final GameHistoryRepository gameHistoryRepository;
    private final RankRepository rankRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;

    /**
     * Cập nhật player profile sau khi hoàn thành game
     * Tự động tính toán: average_score, preferredTopics, total_play_time
     */
    public void updateProfileAfterGame(Long userId, Long roomId) {
        try {
            log.info("Updating player profile for user {} after game in room {}", userId, roomId);

            // Lấy hoặc tạo mới player profile
            PlayerProfile profile = playerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PlayerProfile newProfile = new PlayerProfile();
                    newProfile.setUserId(userId);
                    newProfile.setAge(18); // Default age
                    newProfile.setAverageScore(0.0);
                    newProfile.setPreferredTopics(new ArrayList<>());
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

            // 2. Tính preferred topics (top 3 chủ đề chơi nhiều nhất)
            List<String> preferredTopics = calculatePreferredTopics(userId);
            profile.setPreferredTopics(preferredTopics);
            log.info("Updated preferred topics: {}", preferredTopics);

            // 3. Cập nhật total play time từ ranks table
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
     * Tính toán top 3 chủ đề mà user chơi nhiều nhất
     */
    private List<String> calculatePreferredTopics(Long userId) {
        try {
            // Lấy tất cả user answers
            List<UserAnswer> userAnswers = userAnswerRepository.findByUserId(userId);

            if (userAnswers.isEmpty()) {
                return new ArrayList<>();
            }

            // Đếm số lần xuất hiện của mỗi topic
            Map<Long, Long> topicCounts = userAnswers.stream()
                .map(UserAnswer::getQuestionId)
                .map(questionId -> {
                    try {
                        Question question = questionRepository.findById(questionId).orElse(null);
                        return question != null ? question.getTopicId() : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    topicId -> topicId,
                    Collectors.counting()
                ));

            // Lấy top 3 topics
            List<String> topTopics = topicCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> "Topic " + entry.getKey()) // TODO: Get topic name from Topic entity
                .collect(Collectors.toList());

            log.info("Calculated preferred topics for user {}: {}", userId, topTopics);
            return topTopics;

        } catch (Exception e) {
            log.error("Error calculating preferred topics: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lấy player profile để hiển thị cho frontend
     */
    public PlayerProfile getPlayerProfile(Long userId) {
        return playerProfileRepository.findByUserId(userId)
            .orElse(null);
    }

    /**
     * Khởi tạo player profile cho user mới
     */
    public void initializeProfile(Long userId, Integer age) {
        if (playerProfileRepository.findByUserId(userId).isPresent()) {
            log.info("Profile already exists for user {}", userId);
            return;
        }

        PlayerProfile profile = new PlayerProfile();
        profile.setUserId(userId);
        profile.setAge(age != null ? age : 18);
        profile.setAverageScore(0.0);
        profile.setPreferredTopics(new ArrayList<>());
        profile.setTotalPlayTime(0);

        playerProfileRepository.save(profile);
        log.info("Initialized new player profile for user {}", userId);
    }
}

