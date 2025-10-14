package org.example.quizizz.common.config;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
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

    /**
     * Cấu hình Socket.IO server.
     * @return Socket.IO server
     */
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(port);
        config.setOrigin("http://localhost:5173");

        // Cấp phép quyền truy cập
        config.setTransports(com.corundumstudio.socketio.Transport.WEBSOCKET, com.corundumstudio.socketio.Transport.POLLING);

        // Cấu hình  timeout
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        config.setRandomSession(true);

        // Ping configuration
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        // Authorization listener to check token on connection
        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam("token");
            log.info("Socket.IO auth attempt with token: {}", token != null ? "present" : "missing");
            // Always allow connection, validate token in event handlers
            return new AuthorizationResult(true);
        });

        // Cấu hình Jackson để hỗ trợ Java 8 Date/Time API
        config.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()));

        log.info("Socket.IO server configured on {}:{}", host, port);

        return new SocketIOServer(config);
    }
}
