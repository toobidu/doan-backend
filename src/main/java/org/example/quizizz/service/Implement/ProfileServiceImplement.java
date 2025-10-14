package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.ProfileMapper;
import org.example.quizizz.model.dto.profile.*;
import org.example.quizizz.model.entity.*;
import org.example.quizizz.repository.*;
import org.example.quizizz.service.Interface.IFileStorageService;
import org.example.quizizz.service.Interface.IProfileService;
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
        List<ProfileStatsResponse.AchievementDto> achievements = generateAchievements(userId, gameHistories);
        statsBuilder.achievements(achievements);

        log.info("Profile stats retrieved successfully for user {}", userId);
        return statsBuilder.build();
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
     */
    private List<ProfileStatsResponse.AchievementDto> generateAchievements(Long userId, List<GameHistory> gameHistories) {
        List<ProfileStatsResponse.AchievementDto> achievements = new ArrayList<>();

        int totalGames = gameHistories.size();
        int totalScore = gameHistories.stream().mapToInt(GameHistory::getScore).sum();

        // Achievement 1: Người mới bắt đầu
        achievements.add(ProfileStatsResponse.AchievementDto.builder()
            .name("Người mới bắt đầu")
            .description("Hoàn thành trò chơi đầu tiên")
            .earned(totalGames >= 1)
            .iconUrl(null)
            .build());

        // Achievement 2: Người chơi nhiệt tình
        achievements.add(ProfileStatsResponse.AchievementDto.builder()
            .name("Người chơi nhiệt tình")
            .description("Chơi 10 trò chơi")
            .earned(totalGames >= 10)
            .iconUrl(null)
            .build());

        // Achievement 3: Cao thủ
        achievements.add(ProfileStatsResponse.AchievementDto.builder()
            .name("Cao thủ")
            .description("Đạt 1000 điểm tổng")
            .earned(totalScore >= 1000)
            .iconUrl(null)
            .build());

        // Achievement 4: Huyền thoại
        achievements.add(ProfileStatsResponse.AchievementDto.builder()
            .name("Huyền thoại")
            .description("Chơi 50 trò chơi")
            .earned(totalGames >= 50)
            .iconUrl(null)
            .build());

        return achievements;
    }
}
