package org.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.example.quizizz.controller.socketio.session.SessionManager;
import org.example.quizizz.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConnectionHandler {

    private final JwtUtil jwtUtil;
    private final SessionManager sessionManager;

    public void registerListeners(SocketIOServer server) {
        server.addConnectListener(onConnect());
        server.addDisconnectListener(onDisconnect(server));
    }

    private ConnectListener onConnect() {
        return client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            log.info("🔌 Client {} attempting to connect with token: {}",
                    client.getSessionId(), token != null ? "present" : "missing");

            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                sessionManager.addUserSession(client.getSessionId().toString(), userId);

                // Send welcome message to confirm connection
                client.sendEvent("connection-confirmed", Map.of(
                        "userId", userId,
                        "sessionId", client.getSessionId().toString(),
                        "timestamp", System.currentTimeMillis()));
            } else {
                client.disconnect();
            }
        };
    }

    private DisconnectListener onDisconnect(SocketIOServer server) {
        return client -> {
            String sessionId = client.getSessionId().toString();
            Long userId = sessionManager.getUserId(sessionId);
            Long roomId = sessionManager.getRoomId(sessionId);

            if (userId != null && roomId != null) {
                server.getRoomOperations("room-" + roomId)
                        .sendEvent("player-disconnected", Map.of(
                                "userId", userId,
                                "temporary", true));
                log.info("User {} disconnected from room {} (temporary)", userId, roomId);
            }

            // Clean up session but keep room membership intact
            sessionManager.removeSession(sessionId);
        };
    }
}
