package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.game.*;

public interface IGameService {
    void startGameSession(Long roomId);
    NextQuestionResponse getNextQuestion(Long roomId);
    NextQuestionResponse getCurrentQuestion(Long roomId); // ✅ NEW: Get current question without advancing
    QuestionResultResponse submitAnswer(Long roomId, Long userId, AnswerSubmitRequest request);
    GameOverResponse endGame(Long roomId);
    boolean isGameActive(Long roomId);
    int getRemainingTime(Long roomId);
    Long resolveAnswerId(Long questionId, Integer selectedOptionIndex, String selectedAnswer, String answerText);

    // ✅ REMOVED: Không cần check all players answered cùng lúc
    // boolean haveAllPlayersAnswered(Long roomId, Long questionId);

    // ✅ NEW: Methods cho async gameplay
    NextQuestionResponse getNextQuestionForPlayer(Long roomId, Long userId);
    boolean haveAllPlayersCompleted(Long roomId);
}
