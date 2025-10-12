package org.example.quizizz.service.Implement;

import org.example.quizizz.common.constants.GameStatus;
import org.example.quizizz.common.constants.RoomStatus;
import org.example.quizizz.model.dto.game.*;
import org.example.quizizz.model.dto.game.AnswerOption;
import org.example.quizizz.model.dto.game.PlayerRanking;
import org.example.quizizz.model.dto.game.PlayerScore;
import org.example.quizizz.model.entity.*;
import org.example.quizizz.repository.*;
import org.example.quizizz.service.Interface.IGameService;
import org.example.quizizz.service.Interface.IRedisService;
import org.example.quizizz.service.helper.GameScoreCalculator;
import org.example.quizizz.service.helper.GameTimerService;
import org.example.quizizz.service.helper.GameTimerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImplement implements IGameService {

    private final IRedisService redisService;
    private final GameSessionRepository gameSessionRepository;
    private final GameQuestionRepository gameQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final GameHistoryRepository gameHistoryRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final GameScoreCalculator scoreCalculator;
    private final RankServiceImplement rankService;
    private final ApplicationContext applicationContext;

    // ✅ FIX: Inject SocketIOServer to broadcast events
    private com.corundumstudio.socketio.SocketIOServer socketIOServer;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSocketIOServer(com.corundumstudio.socketio.SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    @Transactional
    public void startGameSession(Long roomId) {
        // Lấy room trước để lấy thông tin
        Room room = roomRepository.findById(roomId).orElseThrow();

        // Tạo game session mới
        GameSession gameSession = new GameSession();
        gameSession.setRoomId(roomId);
        gameSession.setRoomStatus(room.getStatus()); // ✅ FIX: Set roomStatus để tránh constraint error
        gameSession.setGameStatus("IN_PROGRESS");
        gameSession.setStartTime(LocalDateTime.now());
        gameSession = gameSessionRepository.save(gameSession);

        // Lấy danh sách câu hỏi cho topic của room
        List<Question> questions = questionRepository.findQuestionByTopicId(room.getTopicId());

        // Shuffle và lấy số lượng theo cấu hình phòng
        Collections.shuffle(questions);
        int questionCount = room.getQuestionCount();
        questions = questions.subList(0, Math.min(questionCount, questions.size()));

        // Tạo game questions
        for (int i = 0; i < questions.size(); i++) {
            GameQuestion gameQuestion = new GameQuestion();
            gameQuestion.setGameSessionId(gameSession.getId());
            gameQuestion.setQuestionId(questions.get(i).getId());
            gameQuestion.setQuestionOrder(i);
            gameQuestion.setTimeLimit(Duration.ofSeconds(room.getCountdownTime()));
            gameQuestionRepository.save(gameQuestion);
        }

        // Lưu vào Redis
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("gameSessionId", gameSession.getId());
        sessionData.put("currentQuestionIndex", 0);
        sessionData.put("totalQuestions", questions.size());
        sessionData.put("status", GameStatus.IN_PROGRESS.name());
        redisService.saveGameSession("game:" + roomId, sessionData);

        log.info("✅ Started game session {} for room {} with {} questions", gameSession.getId(), roomId, questions.size());
    }

    @Override
    public NextQuestionResponse getNextQuestion(Long roomId) {
        String gameId = "game:" + roomId;
        Map<String, Object> sessionData = redisService.getGameSession(gameId);

        if (sessionData == null || !GameStatus.IN_PROGRESS.name().equals(sessionData.get("status"))) {
            throw new RuntimeException("Game not active");
        }

        int currentIndex = ((Number) sessionData.get("currentQuestionIndex")).intValue();
        int totalQuestions = ((Number) sessionData.get("totalQuestions")).intValue();
        Long gameSessionId = ((Number) sessionData.get("gameSessionId")).longValue();

        if (currentIndex >= totalQuestions) {
            // Game over
            endGame(roomId);
            return null;
        }

        // Lấy thông tin phòng và câu hỏi tiếp theo
        Room room = roomRepository.findById(roomId).orElseThrow();
        List<GameQuestion> gameQuestions = gameQuestionRepository
                .findByGameSessionIdOrderByQuestionOrder(gameSessionId);
        GameQuestion currentGameQuestion = gameQuestions.get(currentIndex);
        Question question = questionRepository.findById(currentGameQuestion.getQuestionId()).orElseThrow();

        // Lấy đáp án
        List<Answer> answers = answerRepository.findByQuestionId(question.getId());
        List<AnswerOption> answerOptions = answers.stream()
                .map(a -> new AnswerOption(a.getId(), a.getAnswerText()))
                .collect(Collectors.toList());

        NextQuestionResponse response = new NextQuestionResponse();
        response.setQuestionId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setAnswers(answerOptions);
        response.setTimeLimit(room.getCountdownTime());
        response.setQuestionNumber(currentIndex + 1);
        response.setTotalQuestions(totalQuestions);

        // Cập nhật current index
        redisService.updateGameSession(gameId, "currentQuestionIndex", currentIndex + 1);

        return response;
    }

    @Override
    public NextQuestionResponse getCurrentQuestion(Long roomId) {
        String gameId = "game:" + roomId;
        Map<String, Object> sessionData = redisService.getGameSession(gameId);

        if (sessionData == null || !GameStatus.IN_PROGRESS.name().equals(sessionData.get("status"))) {
            log.warn("⚠️ Game not active for room {}", roomId);
            return null;
        }

        // ✅ FIX: currentQuestionIndex is the NEXT question to show, so current is index - 1
        int currentIndex = ((Number) sessionData.get("currentQuestionIndex")).intValue();
        int totalQuestions = ((Number) sessionData.get("totalQuestions")).intValue();
        Long gameSessionId = ((Number) sessionData.get("gameSessionId")).longValue();

        // ✅ FIX: If no question has been shown yet, return null
        if (currentIndex <= 0) {
            log.warn("⚠️ No question shown yet for room {}", roomId);
            return null;
        }

        // ✅ FIX: Current question is at index - 1 (since index was already incremented)
        int actualQuestionIndex = currentIndex - 1;

        if (actualQuestionIndex < 0 || actualQuestionIndex >= totalQuestions) {
            log.warn("⚠️ No current question available for room {}, index: {}", roomId, actualQuestionIndex);
            return null;
        }

        // Lấy thông tin phòng và câu hỏi hiện tại
        Room room = roomRepository.findById(roomId).orElseThrow();
        List<GameQuestion> gameQuestions = gameQuestionRepository
                .findByGameSessionIdOrderByQuestionOrder(gameSessionId);
        GameQuestion currentGameQuestion = gameQuestions.get(actualQuestionIndex);
        Question question = questionRepository.findById(currentGameQuestion.getQuestionId()).orElseThrow();

        // Lấy đáp án
        List<Answer> answers = answerRepository.findByQuestionId(question.getId());
        List<AnswerOption> answerOptions = answers.stream()
                .map(a -> new AnswerOption(a.getId(), a.getAnswerText()))
                .collect(Collectors.toList());

        NextQuestionResponse response = new NextQuestionResponse();
        response.setQuestionId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setAnswers(answerOptions);
        response.setTimeLimit(room.getCountdownTime());
        response.setQuestionNumber(actualQuestionIndex + 1);
        response.setTotalQuestions(totalQuestions);

        log.info("✅ Returned current question {} for room {}", actualQuestionIndex + 1, roomId);

        return response;
    }

    @Override
    @Transactional
    public QuestionResultResponse submitAnswer(Long roomId, Long userId, AnswerSubmitRequest request) {
        // Kiểm tra đáp án
        Answer answer = answerRepository.findById(request.getAnswerId()).orElseThrow();
        boolean isCorrect = answer.getIsCorrect();

        // Lấy thông tin phòng để tính điểm
        Room room = roomRepository.findById(roomId).orElseThrow();

        // Tính điểm dựa trên tốc độ và độ chính xác
        int score = scoreCalculator.calculateScore(isCorrect, request.getTimeTaken(), room.getCountdownTime());

        // Lưu user answer
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setQuestionId(request.getQuestionId());
        userAnswer.setRoomId(roomId);
        userAnswer.setAnswerId(request.getAnswerId());
        userAnswer.setIsCorrect(isCorrect);
        userAnswer.setScore(score);
        userAnswer.setTimeTaken(request.getTimeTaken().intValue());
        userAnswerRepository.save(userAnswer);

        QuestionResultResponse response = new QuestionResultResponse();
        response.setIsCorrect(isCorrect);
        response.setScore(score);
        response.setTimeTaken(request.getTimeTaken());
        // Find correct answer
        List<Answer> correctAnswers = answerRepository.findByQuestionId(request.getQuestionId())
                .stream().filter(Answer::getIsCorrect).collect(Collectors.toList());
        Long correctAnswerId = correctAnswers.isEmpty() ? null : correctAnswers.get(0).getId();
        response.setCorrectAnswerId(correctAnswerId);

        return response;
    }

    @Override
    @Transactional
    public GameOverResponse endGame(Long roomId) {
        String gameId = "game:" + roomId;
        Map<String, Object> sessionData = redisService.getGameSession(gameId);

        if (sessionData == null) {
            throw new RuntimeException("Game session not found");
        }

        Long gameSessionId = ((Number) sessionData.get("gameSessionId")).longValue();

        // Cập nhật game session
        GameSession gameSession = gameSessionRepository.findById(gameSessionId).orElseThrow();
        gameSession.setGameStatus(GameStatus.FINISHED.name());
        gameSession.setEndTime(LocalDateTime.now());
        gameSessionRepository.save(gameSession);

        // Tính ranking
        List<UserAnswer> allAnswers = userAnswerRepository.findByRoomId(roomId);
        Map<Long, Integer> userScores = new HashMap<>();
        Map<Long, Long> userTimes = new HashMap<>();
        Map<Long, String> userNames = new HashMap<>();

        // Lấy danh sách players
        List<RoomPlayers> players = roomPlayerRepository.findByRoomId(roomId);
        for (RoomPlayers player : players) {
            userNames.put(player.getUserId(), "User" + player.getUserId()); // TODO: Get from User entity
            userScores.put(player.getUserId(), 0);
            userTimes.put(player.getUserId(), 0L);
        }

        // Tính tổng điểm và thời gian
        for (UserAnswer answer : allAnswers) {
            userScores.put(answer.getUserId(), userScores.get(answer.getUserId()) + answer.getScore());
            userTimes.put(answer.getUserId(), userTimes.get(answer.getUserId()) + answer.getTimeTaken());
        }

        // Tạo ranking
        List<PlayerRanking> rankings = userScores.entrySet().stream()
                .sorted((e1, e2) -> {
                    int scoreCompare = e2.getValue().compareTo(e1.getValue());
                    if (scoreCompare == 0) {
                        return userTimes.get(e1.getKey()).compareTo(userTimes.get(e2.getKey()));
                    }
                    return scoreCompare;
                })
                .map(entry -> {
                    PlayerRanking ranking = new PlayerRanking();
                    ranking.setUserId(entry.getKey());
                    ranking.setUserName(userNames.get(entry.getKey()));
                    ranking.setTotalScore(entry.getValue());
                    ranking.setTotalTime(userTimes.get(entry.getKey()));
                    return ranking;
                })
                .collect(Collectors.toList());

        // Set rank
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        // Tạo user scores
        List<PlayerScore> playerScores = allAnswers.stream()
                .collect(Collectors.groupingBy(UserAnswer::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    PlayerScore score = new PlayerScore();
                    score.setUserId(entry.getKey());
                    score.setUserName(userNames.get(entry.getKey()));
                    score.setScore(userScores.get(entry.getKey()));
                    score.setTimeTaken(userTimes.get(entry.getKey()));
                    return score;
                })
                .collect(Collectors.toList());

        // Lưu game history và cập nhật rank
        int totalQuestions = ((Number) sessionData.get("totalQuestions")).intValue();
        for (PlayerRanking ranking : rankings) {
            GameHistory history = new GameHistory();
            history.setGameSessionId(gameSessionId);
            history.setUserId(ranking.getUserId());
            history.setScore(ranking.getTotalScore().intValue());
            
            long correctCount = allAnswers.stream()
                .filter(a -> a.getUserId().equals(ranking.getUserId()) && a.getIsCorrect())
                .count();
            history.setCorrectAnswers((int) correctCount);
            history.setTotalQuestions(totalQuestions);
            gameHistoryRepository.save(history);
            
            // ✅ UPDATED: Truyền cả totalTime vào updateRankAfterGame
            rankService.updateRankAfterGame(
                ranking.getUserId(),
                ranking.getTotalScore().intValue(),
                ranking.getTotalTime() // Thời gian tổng của game này
            );
        }

        // Cập nhật Redis
        redisService.updateGameStatus(gameId, GameStatus.FINISHED);

        // Cập nhật trạng thái phòng về FINISHED
        Room room = roomRepository.findById(roomId).orElseThrow();
        room.setStatus(RoomStatus.FINISHED.name());
        roomRepository.save(room);

        GameOverResponse response = new GameOverResponse();
        response.setRanking(rankings);
        response.setUserScores(playerScores);

        log.info("Ended game session {} for room {}", gameSessionId, roomId);

        return response;
    }

    @Override
    public boolean isGameActive(Long roomId) {
        String gameId = "game:" + roomId;
        String status = (String) redisService.getGameSession(gameId).get("status");
        return GameStatus.IN_PROGRESS.name().equals(status);
    }

    @Override
    public int getRemainingTime(Long roomId) {
        try {
            GameTimerService gameTimerService = applicationContext.getBean(GameTimerService.class);
            return gameTimerService.getRemainingTime(roomId);
        } catch (Exception e) {
            log.warn("Could not get GameTimerService: {}", e.getMessage());
            return 0;
        }
    }
    
    /*** Xử lý event khi hết thời gian trả lời ***/
    @EventListener
    public void handleTimerFinished(GameTimerEvent event) {
        try {
            Long roomId = event.getRoomId();
            log.info("⏰ Timer finished for room {}, getting next question...", roomId);

            NextQuestionResponse nextQuestion = getNextQuestion(roomId);
            Room room = roomRepository.findById(roomId).orElseThrow();

            if (nextQuestion == null) {
                // Game finished
                log.info("🏁 No more questions, ending game for room {}", roomId);
                GameOverResponse gameResult = endGame(roomId);

                // ✅ FIX: Use room code, not room ID for socket room
                socketIOServer.getRoomOperations("room-" + room.getRoomCode())
                    .sendEvent("game-finished", Map.of(
                        "result", gameResult,
                        "timestamp", System.currentTimeMillis()
                    ));
            } else {
                // Broadcast next question to all players
                log.info("📤 Broadcasting next question {} to room {}", nextQuestion.getQuestionNumber(), roomId);

                // ✅ FIX: Use room code, not room ID for socket room
                socketIOServer.getRoomOperations("room-" + room.getRoomCode())
                    .sendEvent("next-question", Map.of(
                        "question", nextQuestion,
                        "timestamp", System.currentTimeMillis()
                    ));

                // Start timer for next question
                GameTimerService gameTimerService = applicationContext.getBean(GameTimerService.class);
                gameTimerService.startGameTimer(roomId, nextQuestion.getTimeLimit());

                log.info("✅ Successfully sent next question and started timer for room {}", roomId);
            }
        } catch (Exception e) {
            log.error("❌ Error handling timer event: {}", e.getMessage(), e);
        }
    }

    @Override
    public Long resolveAnswerId(Long questionId, Integer selectedOptionIndex, String selectedAnswer, String answerText) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        if (answers == null || answers.isEmpty()) {
            throw new RuntimeException("No answers found for question " + questionId);
        }
        // Prefer match by text first
        if (selectedAnswer != null) {
            Optional<Answer> match = answers.stream()
                    .filter(a -> selectedAnswer.equalsIgnoreCase(a.getAnswerText()))
                    .findFirst();
            if (match.isPresent()) return match.get().getId();
        }
        if (answerText != null) {
            Optional<Answer> match = answers.stream()
                    .filter(a -> answerText.equalsIgnoreCase(a.getAnswerText()))
                    .findFirst();
            if (match.isPresent()) return match.get().getId();
        }
        // Fallback by index if provided (0-based)
        if (selectedOptionIndex != null) {
            // Ensure a deterministic order
            answers.sort(Comparator.comparing(Answer::getId));
            if (selectedOptionIndex >= 0 && selectedOptionIndex < answers.size()) {
                return answers.get(selectedOptionIndex).getId();
            }
        }
        throw new RuntimeException("Cannot resolve answerId from provided selection for question " + questionId);
    }

    @Override
    public NextQuestionResponse getNextQuestionForPlayer(Long roomId, Long userId) {
        try {
            String gameId = "game:" + roomId;
            Map<String, Object> sessionData = redisService.getGameSession(gameId);

            if (sessionData == null || !GameStatus.IN_PROGRESS.name().equals(sessionData.get("status"))) {
                log.warn("⚠️ Game not active for room {}", roomId);
                return null;
            }

            int totalQuestions = ((Number) sessionData.get("totalQuestions")).intValue();
            Long gameSessionId = ((Number) sessionData.get("gameSessionId")).longValue();

            // Đếm số câu hỏi player này đã trả lời
            List<UserAnswer> playerAnswers = userAnswerRepository.findByRoomIdAndUserId(roomId, userId);
            int answeredCount = playerAnswers.size();

            log.info("📊 Player {} has answered {}/{} questions in room {}",
                userId, answeredCount, totalQuestions, roomId);

            // Nếu đã trả lời hết -> return null
            if (answeredCount >= totalQuestions) {
                log.info("🏁 Player {} has completed all questions in room {}", userId, roomId);
                return null;
            }

            // Lấy câu hỏi tiếp theo (index = số câu đã trả lời)
            Room room = roomRepository.findById(roomId).orElseThrow();
            List<GameQuestion> gameQuestions = gameQuestionRepository
                    .findByGameSessionIdOrderByQuestionOrder(gameSessionId);

            if (answeredCount >= gameQuestions.size()) {
                return null;
            }

            GameQuestion nextGameQuestion = gameQuestions.get(answeredCount);
            Question question = questionRepository.findById(nextGameQuestion.getQuestionId()).orElseThrow();

            // Lấy đáp án
            List<Answer> answers = answerRepository.findByQuestionId(question.getId());
            List<AnswerOption> answerOptions = answers.stream()
                    .map(a -> new AnswerOption(a.getId(), a.getAnswerText()))
                    .collect(Collectors.toList());

            NextQuestionResponse response = new NextQuestionResponse();
            response.setQuestionId(question.getId());
            response.setQuestionText(question.getQuestionText());
            response.setAnswers(answerOptions);
            response.setTimeLimit(room.getCountdownTime());
            response.setQuestionNumber(answeredCount + 1);
            response.setTotalQuestions(totalQuestions);

            log.info("✅ Returning question {} for player {} in room {}",
                answeredCount + 1, userId, roomId);

            return response;
        } catch (Exception e) {
            log.error("❌ Error getting next question for player: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean haveAllPlayersCompleted(Long roomId) {
        try {
            String gameId = "game:" + roomId;
            Map<String, Object> sessionData = redisService.getGameSession(gameId);

            if (sessionData == null) {
                log.warn("⚠️ No game session for room {}", roomId);
                return false;
            }

            int totalQuestions = ((Number) sessionData.get("totalQuestions")).intValue();

            // Lấy danh sách players trong room
            List<RoomPlayers> roomPlayers = roomPlayerRepository.findByRoomId(roomId);
            int totalPlayers = roomPlayers.size();

            if (totalPlayers == 0) {
                log.warn("⚠️ No players in room {}", roomId);
                return false;
            }

            // Kiểm tra từng player đã hoàn thành chưa
            int completedPlayers = 0;
            for (RoomPlayers player : roomPlayers) {
                List<UserAnswer> playerAnswers = userAnswerRepository.findByRoomIdAndUserId(roomId, player.getUserId());
                if (playerAnswers.size() >= totalQuestions) {
                    completedPlayers++;
                }
            }

            log.info("📊 Room {}: {}/{} players completed all questions",
                roomId, completedPlayers, totalPlayers);

            return completedPlayers >= totalPlayers;
        } catch (Exception e) {
            log.error("❌ Error checking if all players completed: {}", e.getMessage(), e);
            return false;
        }
    }
}
