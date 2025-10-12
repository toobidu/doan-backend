package org.example.quizizz.model.dto.game;

import java.util.List;

public class GameStateResponse {
    private Long roomId;
    private String gameStatus; // WAITING, STARTING, PLAYING, PAUSED, FINISHED
    private Integer currentQuestionIndex;
    private Integer totalQuestions;
    private Long questionTimeLimit; // seconds
    private Long questionStartTime; // timestamp
    private Long timeRemaining; // seconds remaining for current question
    private List<PlayerGameState> players;
    private Boolean isHost;

    // Constructors
    public GameStateResponse() {
    }

    public GameStateResponse(Long roomId, String gameStatus, Integer currentQuestionIndex,
            Integer totalQuestions, Long questionTimeLimit, Long questionStartTime,
            Long timeRemaining, List<PlayerGameState> players, Boolean isHost) {
        this.roomId = roomId;
        this.gameStatus = gameStatus;
        this.currentQuestionIndex = currentQuestionIndex;
        this.totalQuestions = totalQuestions;
        this.questionTimeLimit = questionTimeLimit;
        this.questionStartTime = questionStartTime;
        this.timeRemaining = timeRemaining;
        this.players = players;
        this.isHost = isHost;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getQuestionTimeLimit() {
        return questionTimeLimit;
    }

    public void setQuestionTimeLimit(Long questionTimeLimit) {
        this.questionTimeLimit = questionTimeLimit;
    }

    public Long getQuestionStartTime() {
        return questionStartTime;
    }

    public void setQuestionStartTime(Long questionStartTime) {
        this.questionStartTime = questionStartTime;
    }

    public Long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public List<PlayerGameState> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerGameState> players) {
        this.players = players;
    }

    public Boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public static class PlayerGameState {
        private Long userId;
        private String username;
        private String displayName;
        private String avatarUrl;
        private Integer score;
        private Integer correctAnswers;
        private Integer totalAnswers;
        private Boolean isReady;
        private Boolean hasAnswered; // for current question
        private String status; // ACTIVE, DISCONNECTED, LEFT
        private Integer rank;
        private Long lastAnswerTime;

        // Constructors
        public PlayerGameState() {
        }

        public PlayerGameState(Long userId, String username, String displayName, String avatarUrl,
                Integer score, Integer correctAnswers, Integer totalAnswers, Boolean isReady,
                Boolean hasAnswered, String status, Integer rank, Long lastAnswerTime) {
            this.userId = userId;
            this.username = username;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.score = score;
            this.correctAnswers = correctAnswers;
            this.totalAnswers = totalAnswers;
            this.isReady = isReady;
            this.hasAnswered = hasAnswered;
            this.status = status;
            this.rank = rank;
            this.lastAnswerTime = lastAnswerTime;
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

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
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

        public Boolean getIsReady() {
            return isReady;
        }

        public void setIsReady(Boolean isReady) {
            this.isReady = isReady;
        }

        public Boolean getHasAnswered() {
            return hasAnswered;
        }

        public void setHasAnswered(Boolean hasAnswered) {
            this.hasAnswered = hasAnswered;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Long getLastAnswerTime() {
            return lastAnswerTime;
        }

        public void setLastAnswerTime(Long lastAnswerTime) {
            this.lastAnswerTime = lastAnswerTime;
        }
    }
}
