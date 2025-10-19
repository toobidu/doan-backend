package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameHistoryResponse {
    private Long id;
    private String topicName;
    private String roomName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Long totalTime;
    private Integer ranking;
    private LocalDateTime playedAt;
}