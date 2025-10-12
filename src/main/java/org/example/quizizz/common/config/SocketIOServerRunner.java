package org.example.quizizz.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(100) // Run after other initializations
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    @Override
    public void run(String... args) {
        try {
            log.info("🚀 Starting Socket.IO server...");
            socketIOServer.start();
            int port = socketIOServer.getConfiguration().getPort();
            log.info("✅ Socket.IO server started successfully on port: {}", port);
            log.info("🔗 Socket.IO server listening at: http://localhost:{}", port);
        } catch (Exception e) {
            log.error("❌ Failed to start Socket.IO server: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start Socket.IO server", e);
        }
    }
}
