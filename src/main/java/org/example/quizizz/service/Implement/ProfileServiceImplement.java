package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.ProfileMapper;
import org.example.quizizz.model.dto.game.GameHistoryResponse;
import org.example.quizizz.model.dto.profile.*;
import org.example.quizizz.model.entity.*;
import org.example.quizizz.repository.*;
import org.example.quizizz.service.Interface.IFileStorageService;
import org.example.quizizz.service.Interface.IProfileService;
import org.example.quizizz.service.helper.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service quản lý thông tin cá nhân người dùng (profile).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImplement implements IProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final IFileStorageService fileStorageService;
    private final GameHistoryRepository gameHistoryRepository;
    private final RankRepository rankRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final GameSessionRepository gameSessionRepository;
    private final RoomRepository roomRepository;
    private final AchievementService achievementService; // Thêm AchievementService

    /**
     * Lấy thông tin profile của người dùng với presigned URL cho avatar.
     * @param userId Id người dùng
     * @return Thông tin profile
     */
    @Override
    public UpdateProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setDob(user.getDob());
        response.setCreatedAt(user.getCreatedAt());
        
        // Tạo presigned URL cho avatar
        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            try {
                response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
            } catch (Exception e) {
                response.setAvatarURL(null);
            }
        }
        
        return response;
    }

    /**
     * Cập nhật thông tin profile của người dùng.
     * @param userId Id người dùng
     * @param request Thông tin cập nhật
     * @return Thông tin profile sau cập nhật
     */
    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        profileMapper.updateUserFromDto(request, user);
        userRepository.save(user);
        
        return getProfile(userId); // Sử dụng lại method getProfile để có presigned URL
    }

    /**
     * Cập nhật avatar cho người dùng.
     * @param userId Id người dùng
     * @param file File avatar
     * @return Thông tin avatar mới
     * @throws Exception Nếu upload lỗi
     */
    @Override
    @Transactional
    public UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        // Upload file mới và lưu tên file vào DB
        String fileName = fileStorageService.uploadAvatar(file, userId);
        user.setAvatarURL(fileName); // Lưu tên file thay vì URL
        userRepository.save(user);
        
        // Không cần cache vì presigned URL có thời hạn
        
        // Tạo presigned URL mới cho response
        String presignedUrl = fileStorageService.getAvatarUrl(fileName);
        UpdateAvatarResponse response = new UpdateAvatarResponse();
        response.setAvatarURL(presignedUrl);
        return response;
    }

    /**
     * Lấy URL avatar của người dùng.
     * @param userId Id người dùng
     * @return Avatar URL
     * @throws Exception Nếu lỗi
     */
    @Override
    public String getAvatarUrl(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        String avatarURL = user.getAvatarURL();
        if (avatarURL == null || avatarURL.isEmpty()) {
            return null;
        }
        
        // Tạo presigned URL mới để đảm bảo không hết hạn
        String fileName = extractFileNameFromUrl(avatarURL);
        return fileStorageService.getAvatarUrl(fileName);
    }
    
    private String extractFileNameFromUrl(String url) {
        if (url == null) return null;
        // Extract filename from MinIO URL (before query parameters)
        String[] parts = url.split("/");
        String fileNameWithParams = parts[parts.length - 1];
        return fileNameWithParams.split("\\?")[0]; // Remove query parameters
    }
    
    @Override
    public List<UserSearchResponse> searchUsers(String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
            .limit(10) // Giới hạn 10 kết quả
            .map(user -> {
                UserSearchResponse response = new UserSearchResponse();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setFullName(user.getFullName());
                
                // Tạo avatar URL nếu có
                if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
                    try {
                        response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
                    } catch (Exception e) {
                        response.setAvatarURL(null);
                    }
                }
                
                return response;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public PublicProfileResponse getPublicProfile(String username) throws Exception {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        
        PublicProfileResponse response = new PublicProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setDob(user.getDob());
        response.setCreatedAt(user.getCreatedAt());
        
        // Tạo avatar URL nếu có
        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
        }
        
        return response;
    }

    /**
     * Lấy thống kê profile của người dùng để hiển thị trên Dashboard và ProfileStats.
     * @param userId Id người dùng
     * @return Thống kê profile
     */
    @Override
    public ProfileStatsResponse getProfileStats(Long userId) {
        log.info("Getting profile stats for user {}", userId);

        ProfileStatsResponse.ProfileStatsResponseBuilder statsBuilder = ProfileStatsResponse.builder();

        // Lấy thông tin rank
        Optional<Rank> rankOpt = rankRepository.findByUserId(userId);
        if (rankOpt.isPresent()) {
            Rank rank = rankOpt.get();
            statsBuilder.gamesPlayed(rank.getGamePlayed());
            statsBuilder.totalScore(rank.getTotalScore());
            statsBuilder.currentRank(calculateCurrentRank(userId, rank.getTotalScore()));

            // Tính fastest time (convert từ milliseconds sang formatted string)
            if (rank.getTotalTime() != null && rank.getTotalTime() > 0 && rank.getGamePlayed() > 0) {
                long avgTimeMs = rank.getTotalTime() / rank.getGamePlayed();
                statsBuilder.fastestTime(formatTime(avgTimeMs));
            } else {
                statsBuilder.fastestTime("N/A");
            }
        } else {
            statsBuilder.gamesPlayed(0);
            statsBuilder.totalScore(0);
            statsBuilder.currentRank(0);
            statsBuilder.fastestTime("N/A");
        }

        // Lấy thông tin game histories
        List<GameHistory> gameHistories = gameHistoryRepository.findByUserId(userId);

        // Tính highest score
        Integer highestScore = gameHistories.stream()
            .map(GameHistory::getScore)
            .max(Integer::compareTo)
            .orElse(0);
        statsBuilder.highestScore(highestScore);

        // Tính average score
        Double averageScore = gameHistories.isEmpty() ? 0.0 :
            gameHistories.stream()
                .mapToInt(GameHistory::getScore)
                .average()
                .orElse(0.0);
        statsBuilder.averageScore(averageScore);

        // Tính highest rank (giả sử ranking càng thấp càng tốt)
        statsBuilder.highestRank(1); // TODO: Implement logic tính highest rank từ game histories

        // Tính medals (số lần đạt top 3)
        statsBuilder.medals(0); // TODO: Implement logic đếm medals

        // Tìm best topic (chủ đề có điểm cao nhất)
        String bestTopic = findBestTopic(userId);
        statsBuilder.bestTopic(bestTopic);

        // Tạo danh sách achievements
        List<ProfileStatsResponse.AchievementDto> achievements = achievementService.generateAchievements(userId, gameHistories);
        statsBuilder.achievements(achievements);

        log.info("Profile stats retrieved successfully for user {}", userId);
        return statsBuilder.build();
    }

    @Override
    public List<GameHistoryResponse> getRecentGameHistory(Long userId) {
        return List.of();
    }

    @Override
    public PlayerStatsResponse getPlayerStats(Long userId) {
        return null;
    }

    /**
     * Tính rank hiện tại dựa trên total score
     */
    private Integer calculateCurrentRank(Long userId, Integer totalScore) {
        List<Rank> allRanks = rankRepository.findAll();
        allRanks.sort((r1, r2) -> r2.getTotalScore().compareTo(r1.getTotalScore()));

        for (int i = 0; i < allRanks.size(); i++) {
            if (allRanks.get(i).getUserId().equals(userId)) {
                return i + 1;
            }
        }
        return allRanks.size() + 1;
    }

    /**
     * Format time từ milliseconds sang string dễ đọc
     */
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        if (seconds < 60) {
            return seconds + "s";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + "m " + remainingSeconds + "s";
        }
    }

    /**
     * Tìm chủ đề tốt nhất của user (chủ đề có tỷ lệ đúng cao nhất)
     */
    private String findBestTopic(Long userId) {
        try {
            List<UserAnswer> userAnswers = userAnswerRepository.findByUserId(userId);

            if (userAnswers.isEmpty()) {
                return "N/A";
            }

            // Nhóm theo topic và tính tỷ lệ đúng
            Map<Long, List<UserAnswer>> answersByTopic = new HashMap<>();

            for (UserAnswer answer : userAnswers) {
                Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
                if (question != null && question.getTopicId() != null) {
                    answersByTopic.computeIfAbsent(question.getTopicId(), k -> new ArrayList<>()).add(answer);
                }
            }

            // Tìm topic có tỷ lệ đúng cao nhất
            Long bestTopicId = null;
            double bestAccuracy = 0.0;

            for (Map.Entry<Long, List<UserAnswer>> entry : answersByTopic.entrySet()) {
                List<UserAnswer> answers = entry.getValue();
                long correctCount = answers.stream().filter(UserAnswer::getIsCorrect).count();
                double accuracy = (double) correctCount / answers.size();

                if (accuracy > bestAccuracy) {
                    bestAccuracy = accuracy;
                    bestTopicId = entry.getKey();
                }
            }

            if (bestTopicId != null) {
                Optional<Topic> topicOpt = topicRepository.findById(bestTopicId);
                if (topicOpt.isPresent()) {
                    return topicOpt.get().getName();
                }
            }

            return "N/A";
        } catch (Exception e) {
            log.error("Error finding best topic: {}", e.getMessage());
            return "N/A";
        }
    }

    /**
     * Tạo danh sách achievements dựa trên thống kê của user
     * Sử dụng AchievementService để tính toán - dễ mở rộng và maintain
     */
//    private List<ProfileStatsResponse.AchievementDto> generateAchievements(Long userId, List<GameHistory> gameHistories) {
//        // Sử dụng AchievementService đã được tách riêng
//        return achievementService.generateAchievements(userId, gameHistories);
//    }
//
//    /**
//     * Lấy lịch sử 5 game gần nhất của người dùng
//     */
//    @Override
//    public List<GameHistoryResponse> getRecentGameHistory(Long userId) {
//        log.info("Getting recent game history for user {}", userId);
//
//        List<GameHistory> gameHistories = gameHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
//
//        return gameHistories.stream().map(gh -> {
//            GameHistoryResponse response = new GameHistoryResponse();
//            response.setId(gh.getId());
//            response.setScore(gh.getScore());
//            response.setCorrectAnswers(gh.getCorrectAnswers());
//            response.setTotalQuestions(gh.getTotalQuestions());
//            response.setPlayedAt(gh.getCreatedAt());
//
//            // Get game session info
//            try {
//                GameSession gameSession = gameSessionRepository.findById(gh.getGameSessionId()).orElse(null);
//                if (gameSession != null) {
//                    // Get room info
//                    Room room = roomRepository.findById(gameSession.getRoomId()).orElse(null);
//                    if (room != null) {
//                        response.setRoomName(room.getRoomName());
//
//                        // Get topic name
//                        Topic topic = topicRepository.findById(room.getTopicId()).orElse(null);
//                        if (topic != null) {
//                            response.setTopicName(topic.getName());
//                        } else {
//                            response.setTopicName("N/A");
//                        }
//                    } else {
//                        response.setRoomName("N/A");
//                        response.setTopicName("N/A");
//                    }
//
//                    // Calculate total time
//                    if (gameSession.getStartTime() != null && gameSession.getEndTime() != null) {
//                        long duration = java.time.Duration.between(gameSession.getStartTime(), gameSession.getEndTime()).getSeconds();
//                        response.setTotalTime(duration);
//                    } else {
//                        response.setTotalTime(0L);
//                    }
//                } else {
//                    response.setRoomName("N/A");
//                    response.setTopicName("N/A");
//                    response.setTotalTime(0L);
//                }
//            } catch (Exception e) {
//                log.error("Error getting game session info: {}", e.getMessage());
//                response.setRoomName("N/A");
//                response.setTopicName("N/A");
//                response.setTotalTime(0L);
//            }
//
//            // Calculate ranking (get all players in the same game session and rank them)
//            try {
//                List<GameHistory> allPlayersInSession = gameHistoryRepository.findByGameSessionId(gh.getGameSessionId());
//                allPlayersInSession.sort((a, b) -> b.getScore().compareTo(a.getScore()));
//
//                int ranking = 1;
//                for (GameHistory history : allPlayersInSession) {
//                    if (history.getId().equals(gh.getId())) {
//                        response.setRanking(ranking);
//                        break;
//                    }
//                    ranking++;
//                }
//            } catch (Exception e) {
//                log.error("Error calculating ranking: {}", e.getMessage());
//                response.setRanking(0);
//            }
//
//            return response;
//        }).collect(Collectors.toList());
//    }
//
//    /**
//     * Lấy thống kê và thành tích của người chơi
//     */
//    @Override
//    public PlayerStatsResponse getPlayerStats(Long userId) {
//        log.info("Getting player stats for user {}", userId);
//
//        PlayerStatsResponse.PlayerStatsResponseBuilder statsBuilder = PlayerStatsResponse.builder();
//
//        // Get highest score
//        Integer highestScore = gameHistoryRepository.findHighestScoreByUserId(userId);
//        statsBuilder.highestScore(highestScore != null ? highestScore : 0);
//
//        // Get total games played
//        Integer totalGames = gameHistoryRepository.countGamesByUserId(userId);
//        statsBuilder.totalGamesPlayed(totalGames != null ? totalGames : 0);
//
//        // Get total correct answers
//        Integer totalCorrectAnswers = gameHistoryRepository.sumCorrectAnswersByUserId(userId);
//        statsBuilder.totalCorrectAnswers(totalCorrectAnswers != null ? totalCorrectAnswers : 0);
//
//        // Get average score
//        Double averageScore = gameHistoryRepository.findAverageScoreByUserId(userId);
//        statsBuilder.averageScore(averageScore != null ? averageScore : 0.0);
//
//        // Calculate accuracy rate
//        List<GameHistory> gameHistories = gameHistoryRepository.findByUserId(userId);
//        if (!gameHistories.isEmpty()) {
//            int totalQuestions = gameHistories.stream().mapToInt(GameHistory::getTotalQuestions).sum();
//            int totalCorrect = gameHistories.stream().mapToInt(GameHistory::getCorrectAnswers).sum();
//            double accuracyRate = totalQuestions > 0 ? (totalCorrect * 100.0 / totalQuestions) : 0.0;
//            statsBuilder.accuracyRate(accuracyRate);
//        } else {
//            statsBuilder.accuracyRate(0.0);
//        }
//
//        // Get highest ranking (lowest rank number is best)
//        Integer highestRanking = null;
//        for (GameHistory gh : gameHistories) {
//            try {
//                List<GameHistory> allPlayersInSession = gameHistoryRepository.findByGameSessionId(gh.getGameSessionId());
//                allPlayersInSession.sort((a, b) -> b.getScore().compareTo(a.getScore()));
//
//                int ranking = 1;
//                for (GameHistory history : allPlayersInSession) {
//                    if (history.getId().equals(gh.getId())) {
//                        if (highestRanking == null || ranking < highestRanking) {
//                            highestRanking = ranking;
//                        }
//                        break;
//                    }
//                    ranking++;
//                }
//            } catch (Exception e) {
//                log.error("Error calculating highest ranking: {}", e.getMessage());
//            }
//        }
//        statsBuilder.highestRanking(highestRanking != null ? highestRanking : 0);
//
//        // Get fastest time (shortest game duration)
//        Long fastestTime = null;
//        for (GameHistory gh : gameHistories) {
//            try {
//                GameSession gameSession = gameSessionRepository.findById(gh.getGameSessionId()).orElse(null);
//                if (gameSession != null && gameSession.getStartTime() != null && gameSession.getEndTime() != null) {
//                    long duration = java.time.Duration.between(gameSession.getStartTime(), gameSession.getEndTime()).getSeconds();
//                    if (fastestTime == null || duration < fastestTime) {
//                        fastestTime = duration;
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Error calculating fastest time: {}", e.getMessage());
//            }
//        }
//        statsBuilder.fastestTime(fastestTime != null ? fastestTime : 0L);
//
//        // Get best topic
//        String bestTopic = findBestTopic(userId);
//        statsBuilder.bestTopic(bestTopic);
//
//        return statsBuilder.build();
//    }
}
