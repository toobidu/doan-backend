package org.example.quizizz.model.dto.socket;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonSetter;

@Data
public class SubmitAnswerSocketRequest {
    private Long roomId;
    private Long questionId;
    private Integer selectedOptionIndex;
    private String selectedAnswer;
    private String answerText;
    private Long timeTaken;

    // ✅ FIX: Handle both Integer and Long from JavaScript
    @JsonSetter("roomId")
    public void setRoomId(Object roomId) {
        if (roomId instanceof Integer) {
            this.roomId = ((Integer) roomId).longValue();
        } else if (roomId instanceof Long) {
            this.roomId = (Long) roomId;
        } else if (roomId instanceof Number) {
            this.roomId = ((Number) roomId).longValue();
        } else if (roomId instanceof String) {
            this.roomId = Long.parseLong((String) roomId);
        }
    }

    @JsonSetter("questionId")
    public void setQuestionId(Object questionId) {
        if (questionId instanceof Integer) {
            this.questionId = ((Integer) questionId).longValue();
        } else if (questionId instanceof Long) {
            this.questionId = (Long) questionId;
        } else if (questionId instanceof Number) {
            this.questionId = ((Number) questionId).longValue();
        } else if (questionId instanceof String) {
            this.questionId = Long.parseLong((String) questionId);
        }
    }

    @JsonSetter("timeTaken")
    public void setTimeTaken(Object timeTaken) {
        if (timeTaken instanceof Integer) {
            this.timeTaken = ((Integer) timeTaken).longValue();
        } else if (timeTaken instanceof Long) {
            this.timeTaken = (Long) timeTaken;
        } else if (timeTaken instanceof Number) {
            this.timeTaken = ((Number) timeTaken).longValue();
        } else if (timeTaken instanceof String) {
            this.timeTaken = Long.parseLong((String) timeTaken);
        }
    }
}
