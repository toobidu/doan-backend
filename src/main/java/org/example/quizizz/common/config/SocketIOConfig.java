package org.example.quizizz.common.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SocketIOConfig {

    @Value("${socketio.server.hostname:localhost}")
    private String host;

    @Value("${socketio.server.port:9093}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0"); // Listen on all interfaces
        config.setPort(port);
        config.setOrigin("http://localhost:5173"); // Frontend URL

        // Allow all transports
        config.setTransports(com.corundumstudio.socketio.Transport.WEBSOCKET, com.corundumstudio.socketio.Transport.POLLING);

        // Connection configuration
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        config.setRandomSession(true);

        // Timeouts for real-time gaming
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        // Authentication - Allow all connections (token validation done in event handlers)
        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam("token");
            log.info("Socket.IO auth attempt with token: {}", token != null ? "present" : "missing");
            // Always allow connection, validate token in event handlers
            return new com.corundumstudio.socketio.AuthorizationResult(true);
        });

        // Configure Jackson to handle Java 8 date/time types (LocalDateTime, etc.)
        config.setJsonSupport(new com.corundumstudio.socketio.protocol.JacksonJsonSupport(new JavaTimeModule()));

        log.info("Socket.IO server configured on {}:{}", host, port);

        return new SocketIOServer(config);
    }
}
