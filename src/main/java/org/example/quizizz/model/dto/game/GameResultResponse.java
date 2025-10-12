package org.example.quizizz.model.dto.game;

import java.util.List;

public class GameResultResponse {
    private Long roomId;
    private String roomName;
    private Integer totalQuestions;
    private Long gameDuration; // milliseconds
    private List<PlayerResult> rankings;
    private String topicName;
    private Long gameEndTime;

    // Constructors
    public GameResultResponse() {
    }

    public GameResultResponse(Long roomId, String roomName, Integer totalQuestions,
            Long gameDuration, List<PlayerResult> rankings,
            String topicName, Long gameEndTime) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalQuestions = totalQuestions;
        this.gameDuration = gameDuration;
        this.rankings = rankings;
        this.topicName = topicName;
        this.gameEndTime = gameEndTime;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getGameDuration() {
        return gameDuration;
    }

    public void setGameDuration(Long gameDuration) {
        this.gameDuration = gameDuration;
    }

    public List<PlayerResult> getRankings() {
        return rankings;
    }

    public void setRankings(List<PlayerResult> rankings) {
        this.rankings = rankings;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getGameEndTime() {
        return gameEndTime;
    }

    public void setGameEndTime(Long gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public static class PlayerResult {
        private Long userId;
        private String username;
        private String displayName;
        private String avatarUrl;
        private Integer finalScore;
        private Integer correctAnswers;
        private Integer totalAnswers;
        private Double accuracy; // percentage
        private Long averageResponseTime; // milliseconds
        private Integer rank;

        // Constructors
        public PlayerResult() {
        }

        public PlayerResult(Long userId, String username, String displayName, String avatarUrl,
                Integer finalScore, Integer correctAnswers, Integer totalAnswers,
                Double accuracy, Long averageResponseTime, Integer rank) {
            this.userId = userId;
            this.username = username;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.finalScore = finalScore;
            this.correctAnswers = correctAnswers;
            this.totalAnswers = totalAnswers;
            this.accuracy = accuracy;
            this.averageResponseTime = averageResponseTime;
            this.rank = rank;
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public Integer getFinalScore() {
            return finalScore;
        }

        public void setFinalScore(Integer finalScore) {
            this.finalScore = finalScore;
        }

        public Integer getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(Integer correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public Integer getTotalAnswers() {
            return totalAnswers;
        }

        public void setTotalAnswers(Integer totalAnswers) {
            this.totalAnswers = totalAnswers;
        }

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }

        public Long getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(Long averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }
    }
}
