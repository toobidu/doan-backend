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

/**
 * Xử lý kết nối và ngắt kết nối
 * - Xác thực JWT khi kết nối
 * - Quản lý phiên người dùng
 * - Thông báo ngắt kết nối tạm thời
 * - Giữ nguyên trạng thái phòng khi ngắt kết nối tạm thời
 */
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

    // Xử lý sự kiện kết nối
    private ConnectListener onConnect() {
        return client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            log.info("🔌 Client {} attempting to connect with token: {}",
                    client.getSessionId(), token != null ? "present" : "missing");

            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                sessionManager.addUserSession(client.getSessionId().toString(), userId);

                // Gửi xác nhận kết nối thành công
                client.sendEvent("connection-confirmed", Map.of(
                        "userId", userId,
                        "sessionId", client.getSessionId().toString(),
                        "timestamp", System.currentTimeMillis()));
            } else {
                client.disconnect();
            }
        };
    }

    // Xử lý sự kiện ngắt kết nối
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

            // Xóa phiên người dùng
            sessionManager.removeSession(sessionId);
        };
    }
}
