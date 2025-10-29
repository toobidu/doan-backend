package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.game.*;

public interface IGameService {
    void startGameSession(Long roomId);
    NextQuestionResponse getNextQuestion(Long roomId);
    NextQuestionResponse getCurrentQuestion(Long roomId);
    QuestionResultResponse submitAnswer(Long roomId, Long userId, AnswerSubmitRequest request);
    GameOverResponse endGame(Long roomId);
    Long resolveAnswerId(Long questionId, Integer selectedOptionIndex, String selectedAnswer, String answerText);
    NextQuestionResponse getNextQuestionForPlayer(Long roomId, Long userId);
    boolean haveAllPlayersCompleted(Long roomId);
}
