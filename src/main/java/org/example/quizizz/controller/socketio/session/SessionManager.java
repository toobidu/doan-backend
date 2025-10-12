package org.example.quizizz.controller.socketio.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {

    private final Map<String, Long> sessionToUserId = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToRoomId = new ConcurrentHashMap<>();

    public void addUserSession(String sessionId, Long userId) {
        sessionToUserId.put(sessionId, userId);
        log.debug("Added user session: {} -> {}", sessionId, userId);
    }

    public void addRoomSession(String sessionId, Long roomId) {
        sessionToRoomId.put(sessionId, roomId);
        log.debug("Added room session: {} -> {}", sessionId, roomId);
    }

    public Long getUserId(String sessionId) {
        return sessionToUserId.get(sessionId);
    }

    public Long getUserId(UUID sessionId) {
        return sessionToUserId.get(sessionId.toString());
    }

    public Long getRoomId(String sessionId) {
        return sessionToRoomId.get(sessionId);
    }

    public void removeSession(String sessionId) {
        Long userId = sessionToUserId.remove(sessionId);
        Long roomId = sessionToRoomId.remove(sessionId);
        log.debug("Removed session: {} (user: {}, room: {})", sessionId, userId, roomId);
    }

    public void removeRoomSession(String sessionId) {
        sessionToRoomId.remove(sessionId);
    }
}
