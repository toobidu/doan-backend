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
}
