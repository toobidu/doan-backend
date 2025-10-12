package org.example.quizizz.service.Implement;

import org.example.quizizz.common.constants.GameStatus;
import org.example.quizizz.common.constants.PermissionCode;
import org.example.quizizz.common.constants.RedisKeyPrefix;
import org.example.quizizz.service.Interface.IRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisServiceImplement implements IRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lưu quyền cho người dùng
     * @param userId
     * @param permissions
     */
    @Override
    public void saveUserPermissions(Long userId, Set<PermissionCode> permissions) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        try {
            redisTemplate.delete(key);
            if (permissions != null && !permissions.isEmpty()) {
                // Sửa tại đây: dùng getCode() thay vì name()
                Set<String> permissionCodes = permissions.stream()
                        .map(PermissionCode::getCode)
                        .collect(Collectors.toSet());
                permissionCodes.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving permissions to Redis", e);
        }
    }

    /**
     * Thêm quyền cho người dùng
     * @param userId
     * @param newPermissions
     */
    @Override
    public void addPermissions(Long userId, Set<PermissionCode> newPermissions) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        try {
            if (newPermissions != null && !newPermissions.isEmpty()) {
                Set<String> permissionCodes = newPermissions.stream()
                        .map(PermissionCode::getCode)
                        .collect(Collectors.toSet());
                permissionCodes.forEach(p -> redisTemplate.opsForSet().add(key, p));
                redisTemplate.expire(key, 24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding permissions", e);
        }
    }

    /**
     * Xóa quyền cho người dùng
     * @param userId
     * @param permissionsToRemove
     */
    @Override
    public void removePermissions(Long userId, Set<PermissionCode> permissionsToRemove) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        try {
            if (permissionsToRemove != null && !permissionsToRemove.isEmpty()) {
                // Sửa tại đây: dùng getCode() thay vì name()
                String[] permissionCodes = permissionsToRemove.stream()
                        .map(PermissionCode::getCode)
                        .toArray(String[]::new);
                redisTemplate.opsForSet().remove(key, (Object[]) permissionCodes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error removing permissions", e);
        }
    }

    /**
     * Lấy quyền của người dùng
     * @param userId
     * @return
     */
    @Override
    public Set<PermissionCode> getUserPermissions(Long userId) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        try {
            Set<Object> data = redisTemplate.opsForSet().members(key);
            if (data == null || data.isEmpty()) return Collections.emptySet();

            // Sửa tại đây: map từ code sang enum
            return data.stream()
                    .map(Object::toString)
                    .map(code -> {
                        for (PermissionCode p : PermissionCode.values()) {
                            if (p.getCode().equals(code)) return p;
                        }
                        return null;
                    })
                    .filter(permission -> permission != null)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    /**
     * Kiểm tra quyền của người dùng
     * @param userId
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(Long userId, PermissionCode permission) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        try {
            // Sửa tại đây: dùng getCode() thay vì name()
            Boolean isMember = redisTemplate.opsForSet().isMember(key, permission.getCode());
            return Boolean.TRUE.equals(isMember);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteUserPermissionsCache(Long userId) {
        String key = RedisKeyPrefix.USER_PERMISSIONS.format(userId);
        redisTemplate.delete(key);
    }

    /**
     * Lưu game session
     * @param gameId
     * @param sessionData
     */
    @Override
    public void saveGameSession(String gameId, Map<String, Object> sessionData) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        try {
            redisTemplate.opsForHash().putAll(key, sessionData);
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error saving game session", e);
        }
    }

    /**
     * Lấy game session
     * @param gameId
     * @return
     */
    @Override
    public Map<String, Object> getGameSession(String gameId) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            if (entries == null || entries.isEmpty()) return Collections.emptyMap();
            return entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * Cập nhật game session
     * @param gameId
     * @param field
     * @param value
     */
    @Override
    public void updateGameSession(String gameId, String field, Object value) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        try {
            redisTemplate.opsForHash().put(key, field, value);
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error updating game session", e);
        }
    }

    /**
     * Cập nhật trạng thái game
     * @param gameId
     * @param status
     */
    @Override
    public void updateGameStatus(String gameId, GameStatus status) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        try {
            redisTemplate.opsForHash().put(key, "status", status.name());
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error updating game status", e);
        }
    }

    /**
     * Lấy trạng thái game
     * @param gameId
     * @return
     */
    @Override
    public GameStatus getGameStatus(String gameId) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        try {
            Object status = redisTemplate.opsForHash().get(key, "status");
            if (status != null) {
                return GameStatus.valueOf(status.toString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Xóa game session
     * @param gameId
     */
    @Override
    public void deleteGameSession(String gameId) {
        String key = RedisKeyPrefix.GAME_SESSION.format(gameId);
        redisTemplate.delete(key);
    }

    /**
     * Thêm người chơi vào game
     * @param gameId
     * @param userId
     * @param playerName
     */
    @Override
    public void addPlayerToGame(String gameId, Long userId, String playerName) {
        String key = RedisKeyPrefix.GAME_PLAYERS.format(gameId);
        try {
            redisTemplate.opsForHash().put(key, userId.toString(), playerName);
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error adding player to game", e);
        }
    }

    /**
     * Xóa người chơi khỏi game
     * @param gameId
     * @param userId
     */
    @Override
    public void removePlayerFromGame(String gameId, Long userId) {
        String key = RedisKeyPrefix.GAME_PLAYERS.format(gameId);
        redisTemplate.opsForHash().delete(key, userId.toString());
    }

    @Override
    public Set<String> getPlayersInGame(String gameId) {
        String key = RedisKeyPrefix.GAME_PLAYERS.format(gameId);
        try {
            return redisTemplate.opsForHash().keys(key).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean isPlayerInGame(String gameId, Long userId) {
        String key = RedisKeyPrefix.GAME_PLAYERS.format(gameId);
        return redisTemplate.opsForHash().hasKey(key, userId.toString());
    }

    @Override
    public int getPlayerCount(String gameId) {
        String key = RedisKeyPrefix.GAME_PLAYERS.format(gameId);
        return redisTemplate.opsForHash().size(key).intValue();
    }

    /**
     * Cập nhật điểm của người chơi
     * @param gameId
     * @param userId
     * @param score
     */
    @Override
    public void updatePlayerScore(String gameId, Long userId, Double score) {
        String key = RedisKeyPrefix.GAME_SCORES.format(gameId);
        try {
            redisTemplate.opsForZSet().add(key, userId.toString(), score);
            redisTemplate.expire(key, 2, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error updating player score", e);
        }
    }

    @Override
    public Set<Object> getTopPlayers(String gameId, int topN) {
        String key = RedisKeyPrefix.GAME_SCORES.format(gameId);
        try {
            return redisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public Double getPlayerScore(String gameId, Long userId) {
        String key = RedisKeyPrefix.GAME_SCORES.format(gameId);
        return redisTemplate.opsForZSet().score(key, userId.toString());
    }

    @Override
    public void deleteGameScores(String gameId) {
        String key = RedisKeyPrefix.GAME_SCORES.format(gameId);
        redisTemplate.delete(key);
    }

    // ============ QUIZ CACHE ============
    @Override
    public void cacheQuiz(String quizId, Object quizData) {
        String key = RedisKeyPrefix.QUIZ_CACHE.format(quizId);
        try {
            redisTemplate.opsForValue().set(key, quizData, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new RuntimeException("Error caching quiz", e);
        }
    }

    @Override
    public Object getCachedQuiz(String quizId) {
        String key = RedisKeyPrefix.QUIZ_CACHE.format(quizId);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteCachedQuiz(String quizId) {
        String key = RedisKeyPrefix.QUIZ_CACHE.format(quizId);
        redisTemplate.delete(key);
    }

    // ============ TOKEN BLACKLIST ============
    @Override
    public void addTokenToBlacklist(String token, long expiration) {
        String key = RedisKeyPrefix.TOKEN_BLACKLIST.format(token);
        try {
            // Tính thời gian còn lại từ hiện tại đến khi token hết hạn
            long currentTime = System.currentTimeMillis();
            long remainingTime = expiration - currentTime;

            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", remainingTime, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding token to blacklist", e);
        }
    }

    @Override
    public void addTokenToBlacklistWithRefreshTTL(String token, long refreshTokenExpiration) {
        String key = RedisKeyPrefix.TOKEN_BLACKLIST.format(token);
        try {
            // Sử dụng thời gian hết hạn của refresh token làm TTL
            long currentTime = System.currentTimeMillis();
            long remainingTime = refreshTokenExpiration - currentTime;

            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", remainingTime, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding token to blacklist with refresh TTL", e);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = RedisKeyPrefix.TOKEN_BLACKLIST.format(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ============ USER ONLINE STATUS ============
    @Override
    public void setUserOnline(Long userId) {
        String key = RedisKeyPrefix.USERS_ONLINE.getPattern();
        redisTemplate.opsForSet().add(key, userId.toString());
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    @Override
    public void setUserOffline(Long userId) {
        String key = RedisKeyPrefix.USERS_ONLINE.getPattern();
        redisTemplate.opsForSet().remove(key, userId.toString());
    }

    @Override
    public Set<String> getOnlineUsers() {
        String key = RedisKeyPrefix.USERS_ONLINE.getPattern();
        try {
            Set<Object> data = redisTemplate.opsForSet().members(key);
            if (data == null) return Collections.emptySet();
            return data.stream().map(Object::toString).collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean isUserOnline(Long userId) {
        String key = RedisKeyPrefix.USERS_ONLINE.getPattern();
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }

    // ============ GENERIC CACHE ============
    @Override
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValue(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
