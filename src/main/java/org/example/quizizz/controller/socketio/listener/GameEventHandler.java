package org.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOServer;
import org.example.quizizz.controller.socketio.session.SessionManager;
import org.example.quizizz.model.dto.game.*;
import org.example.quizizz.model.dto.socket.NextQuestionRequest;
import org.example.quizizz.model.dto.socket.StartGameRequest;
import org.example.quizizz.model.dto.socket.SubmitAnswerSocketRequest;
import org.example.quizizz.model.entity.Room;
import org.example.quizizz.repository.RoomRepository;
import org.example.quizizz.service.Interface.IGameService;
import org.example.quizizz.service.Interface.IRoomService;
import org.example.quizizz.service.helper.GameTimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameEventHandler {

    private final IGameService gameService;
    private final IRoomService roomService;
    private final SessionManager sessionManager;
    private final RoomRepository roomRepository;
    private final ApplicationContext applicationContext;

    /*** Đăng ký các sự kiện game ***/
    public void registerEvents(SocketIOServer server) {

        /*** Bắt đầu game - chỉ host mới được phép ***/
        server.addEventListener("start-game", StartGameRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null) {
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", false,
                                "message", "User not authenticated"
                        ));
                    }
                    client.sendEvent("error", Map.of("message", "User not authenticated"));
                    return;
                }

                if (!roomService.isRoomHost(data.getRoomId(), userId)) {
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", false,
                                "message", "Only host can start game"
                        ));
                    }
                    client.sendEvent("error", Map.of("message", "Only host can start game"));
                    return;
                }

                log.info("User {} starting game in room {}", userId, data.getRoomId());

                // Bắt đầu game session
                gameService.startGameSession(data.getRoomId());
                roomService.startGame(data.getRoomId(), userId);

                Room room = roomRepository.findById(data.getRoomId()).orElseThrow();

                // Lấy câu hỏi đầu tiên
                NextQuestionResponse firstQuestion = gameService.getNextQuestion(data.getRoomId());

                if (firstQuestion != null) {
                    log.info("First question loaded: {}", firstQuestion.getQuestionText());

                    // Broadcast game bắt đầu đến tất cả players
                    server.getRoomOperations("room-" + room.getRoomCode())
                            .sendEvent("game-started", Map.of(
                                    "roomId", data.getRoomId(),
                                    "question", firstQuestion,
                                    "timestamp", System.currentTimeMillis()
                            ));

                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", true,
                                "roomId", data.getRoomId(),
                                "question", firstQuestion
                        ));
                    }

                    // Bắt đầu đếm ngược
                    GameTimerService gameTimerService = applicationContext.getBean(GameTimerService.class);
                    gameTimerService.startGameTimer(data.getRoomId(), firstQuestion.getTimeLimit());
                } else {
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", false,
                                "message", "No questions available"
                        ));
                    }
                    client.sendEvent("error", Map.of("message", "No questions available"));
                }

            } catch (Exception e) {
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(Map.of(
                            "success", false,
                            "message", "Failed to start game: " + e.getMessage()
                    ));
                }
                client.sendEvent("error", Map.of("message", "Failed to start game: " + e.getMessage()));
            }
        });

        /*** Gửi đáp án ***/
        server.addEventListener("submit-answer", SubmitAnswerSocketRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null) {
                    client.sendEvent("error", Map.of("message", "User not authenticated"));
                    return;
                }

                log.info("📝 User {} submitting answer for question {} in room {}",
                    userId, data.getQuestionId(), data.getRoomId());

                Long answerId = gameService.resolveAnswerId(
                        data.getQuestionId(),
                        data.getSelectedOptionIndex(),
                        data.getSelectedAnswer(),
                        data.getAnswerText()
                );

                AnswerSubmitRequest request = new AnswerSubmitRequest();
                request.setQuestionId(data.getQuestionId());
                request.setAnswerId(answerId);
                request.setTimeTaken(data.getTimeTaken());

                // Gửi đáp án và nhận kết quả
                QuestionResultResponse result = gameService.submitAnswer(data.getRoomId(), userId, request);

                log.info("✅ Answer result for user {}: isCorrect={}, score={}, streak={}",
                    userId, result.getIsCorrect(), result.getScore(), result.getStreak());

                // Lấy câu hỏi tiếp theo cho player này
                NextQuestionResponse nextQuestion = gameService.getNextQuestionForPlayer(data.getRoomId(), userId);

                if (nextQuestion != null) {
                    log.info("📤 Sending next question {} to user {}", nextQuestion.getQuestionNumber(), userId);

                    // Còn câu hỏi tiếp theo - gửi cho player này
                    client.sendEvent("answer-submitted", Map.of(
                            "result", result,
                            "nextQuestion", nextQuestion,
                            "hasNextQuestion", true,
                            "timestamp", System.currentTimeMillis()
                    ));
                } else {
                    log.info("🏁 User {} completed all questions in room {}", userId, data.getRoomId());

                    // Hết câu hỏi - player này đã hoàn thành
                    client.sendEvent("answer-submitted", Map.of(
                            "result", result,
                            "hasNextQuestion", false,
                            "completed", true,
                            "timestamp", System.currentTimeMillis()
                    ));

                    // Kiểm tra xem tất cả players đã hoàn thành chưa
                    boolean allCompleted = gameService.haveAllPlayersCompleted(data.getRoomId());
                    log.info("🎯 Room {}: All players completed? {}", data.getRoomId(), allCompleted);

                    if (allCompleted) {
                        log.info("🎉 All players completed! Ending game for room {}", data.getRoomId());

                        // Tính toán kết quả cuối cùng
                        GameOverResponse gameResult = gameService.endGame(data.getRoomId());

                        Room room = roomRepository.findById(data.getRoomId()).orElseThrow();
                        // Broadcast kết quả đến tất cả players
                        server.getRoomOperations("room-" + room.getRoomCode())
                                .sendEvent("game-finished", Map.of(
                                        "result", gameResult,
                                        "timestamp", System.currentTimeMillis()
                                ));
                    }
                }

                Room room = roomRepository.findById(data.getRoomId()).orElseThrow();
                // Broadcast rằng user đã trả lời (không tiết lộ đáp án)
                server.getRoomOperations("room-" + room.getRoomCode())
                        .sendEvent("player-answered", Map.of(
                                "userId", userId,
                                "timestamp", System.currentTimeMillis()
                        ));

            } catch (Exception e) {
                log.error("❌ Error submitting answer for user: {}", e.getMessage(), e);
                client.sendEvent("error", Map.of("message", "Failed to submit answer: " + e.getMessage()));
            }
        });

        /*** Hiển thị kết quả câu hỏi - chỉ host ***/
        server.addEventListener("show-question-result", NextQuestionRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null || !roomService.isRoomHost(data.getRoomId(), userId)) {
                    client.sendEvent("error", Map.of("message", "Only host can control game"));
                    return;
                }

                Room room = roomRepository.findById(data.getRoomId()).orElseThrow();
                // Broadcast kết quả câu hỏi đến tất cả players
                server.getRoomOperations("room-" + room.getRoomCode())
                        .sendEvent("question-result-shown", Map.of(
                                "roomId", data.getRoomId(),
                                "timestamp", System.currentTimeMillis()
                        ));

            } catch (Exception e) {
                log.error("Error showing question result: {}", e.getMessage());
            }
        });

        /*** Chuyển câu hỏi tiếp theo - chỉ host ***/
        server.addEventListener("next-question", NextQuestionRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null || !roomService.isRoomHost(data.getRoomId(), userId)) {
                    client.sendEvent("error", Map.of("message", "Only host can control game"));
                    return;
                }

                Long roomId = data.getRoomId();
                NextQuestionResponse nextQuestion = gameService.getNextQuestion(roomId);
                Room room = roomRepository.findById(roomId).orElseThrow();

                if (nextQuestion != null) {
                    // Broadcast câu hỏi tiếp theo
                    server.getRoomOperations("room-" + room.getRoomCode())
                            .sendEvent("next-question", Map.of(
                                    "question", nextQuestion,
                                    "timestamp", System.currentTimeMillis()
                            ));

                    // Bắt đầu đếm ngược mới
                    GameTimerService gameTimerService = applicationContext.getBean(GameTimerService.class);
                    gameTimerService.startGameTimer(roomId, nextQuestion.getTimeLimit());
                } else {
                    // Kết thúc game
                    GameOverResponse gameResult = gameService.endGame(roomId);
                    server.getRoomOperations("room-" + room.getRoomCode())
                            .sendEvent("game-finished", Map.of(
                                    "result", gameResult,
                                    "timestamp", System.currentTimeMillis()
                            ));
                }

            } catch (Exception e) {
                log.error("Error getting next question: {}", e.getMessage());
                client.sendEvent("error", Map.of("message", "Failed to get next question"));
            }
        });

        /*** Kết thúc game sớm - chỉ host ***/
        server.addEventListener("end-game", StartGameRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null || !roomService.isRoomHost(data.getRoomId(), userId)) {
                    client.sendEvent("error", Map.of("message", "Only host can end game"));
                    return;
                }

                // Dừng timer
                GameTimerService gameTimerService = applicationContext.getBean(GameTimerService.class);
                gameTimerService.stopGameTimer(data.getRoomId());

                // Kết thúc game
                GameOverResponse gameResult = gameService.endGame(data.getRoomId());
                Room room = roomRepository.findById(data.getRoomId()).orElseThrow();
                server.getRoomOperations("room-" + room.getRoomCode())
                        .sendEvent("game-finished", Map.of(
                                "result", gameResult,
                                "timestamp", System.currentTimeMillis()
                        ));

            } catch (Exception e) {
                log.error("Error ending game: {}", e.getMessage());
                client.sendEvent("error", Map.of("message", "Failed to end game"));
            }
        });

        server.addEventListener("get-game-state", StartGameRequest.class, (client, data, ackRequest) -> {
            try {
                Long userId = sessionManager.getUserId(client.getSessionId());
                if (userId == null) {
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", false,
                                "message", "User not authenticated"
                        ));
                    }
                    return;
                }

                Long roomId = data.getRoomId();

                // Lấy câu hỏi hiện tại
                NextQuestionResponse currentQuestion = gameService.getCurrentQuestion(roomId);

                if (currentQuestion != null) {
                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", true,
                                "currentQuestion", currentQuestion,
                                "timestamp", System.currentTimeMillis()
                        ));
                    }
                } else {
                    log.warn("⚠️ No current question found for room {}", roomId);

                    if (ackRequest.isAckRequested()) {
                        ackRequest.sendAckData(Map.of(
                                "success", false,
                                "message", "No current question available"
                        ));
                    }
                }

            } catch (Exception e) {
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData(Map.of(
                            "success", false,
                            "message", "Failed to get game state: " + e.getMessage()
                    ));
                }
            }
        });
    }
}
