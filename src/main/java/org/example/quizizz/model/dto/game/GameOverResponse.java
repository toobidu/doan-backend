package org.example.quizizz.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameOverResponse {
    private List<PlayerRanking> ranking;
    private List<PlayerScore> userScores;


}
