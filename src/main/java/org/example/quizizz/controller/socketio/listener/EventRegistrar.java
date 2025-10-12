package org.example.quizizz.controller.socketio.listener;

import com.corundumstudio.socketio.SocketIOServer;
import org.example.quizizz.controller.socketio.SocketIOEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventRegistrar {

    private final RoomListEventHandler roomListEventHandler;
    private final RoomEventHandler roomEventHandler;
    private final GameEventHandler gameEventHandler;

    public void registerEvents(SocketIOServer server, SocketIOEventHandler handler) {
        // Debug and test events
        server.addEventListener("ping", Object.class,
                (client, data, ackSender) -> {
                    log.info("Received ping from client: {}", client.getSessionId());
                    ackSender.sendAckData("pong", System.currentTimeMillis());
                });

        server.addEventListener("test-connection", Object.class,
                (client, data, ackSender) -> {
                    log.info("Received test-connection from client: {}", client.getSessionId());
                    client.sendEvent("test-response", "Backend received your test");
                });

        // Register handlers
        roomListEventHandler.registerEvents(server);
        roomEventHandler.registerEvents(server);
        gameEventHandler.registerEvents(server);

        log.info("Socket.IO events registered successfully");
    }
}
