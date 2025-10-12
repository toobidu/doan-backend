package org.example.quizizz.service.Interface;

import org.example.quizizz.common.constants.GameStatus;
import org.example.quizizz.common.constants.PermissionCode;

import java.util.Map;
import java.util.Set;

public interface IRedisService {

    /**
     * User permissions
     */
    void saveUserPermissions(Long userId, Set<PermissionCode> permissions);

    void addPermissions(Long userId, Set<PermissionCode> newPermissions);

    void removePermissions(Long userId, Set<PermissionCode> permissionsToRemove);

    Set<PermissionCode> getUserPermissions(Long userId);

    boolean hasPermission(Long userId, PermissionCode permission);

    void deleteUserPermissionsCache(Long userId);

    /**
     * Game session
     */
    void saveGameSession(String gameId, Map<String, Object> sessionData);

    Map<String, Object> getGameSession(String gameId);

    void updateGameSession(String gameId, String field, Object value);

    void updateGameStatus(String gameId, GameStatus status);

    GameStatus getGameStatus(String gameId);

    void deleteGameSession(String gameId);

    /**
     * Game players
     */
    void addPlayerToGame(String gameId, Long userId, String playerName);

    void removePlayerFromGame(String gameId, Long userId);

    Set<String> getPlayersInGame(String gameId);

    boolean isPlayerInGame(String gameId, Long userId);

    int getPlayerCount(String gameId);

    /**
     * Game scores (Leaderboard)
     */
    void updatePlayerScore(String gameId, Long userId, Double score);

    Set<Object> getTopPlayers(String gameId, int topN);

    Double getPlayerScore(String gameId, Long userId);

    void deleteGameScores(String gameId);

    /**
     * Quiz cache
     */
    void cacheQuiz(String quizId, Object quizData);

    Object getCachedQuiz(String quizId);

    void deleteCachedQuiz(String quizId);

    /**
     * Token blacklist
     */
    void addTokenToBlacklist(String token, long expiration);

    void addTokenToBlacklistWithRefreshTTL(String token, long refreshTokenExpiration);

    boolean isTokenBlacklisted(String token);

    /**
     * Online users
     */
    void setUserOnline(Long userId);

    void setUserOffline(Long userId);

    Set<String> getOnlineUsers();

    boolean isUserOnline(Long userId);

    /**
     * Generic cache operations
     */
    void setValue(String key, Object value);

    void setValue(String key, Object value, long timeoutInSeconds);

    Object getValue(String key);

    void deleteKey(String key);

    boolean hasKey(String key);
}
