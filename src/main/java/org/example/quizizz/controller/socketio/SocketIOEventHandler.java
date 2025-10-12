package org.example.quizizz.controller.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import org.example.quizizz.controller.socketio.listener.ConnectionHandler;
import org.example.quizizz.controller.socketio.listener.EventRegistrar;
import org.example.quizizz.controller.socketio.listener.RoomEventHandler;
import org.example.quizizz.controller.socketio.listener.RoomListEventHandler;
import org.example.quizizz.controller.socketio.session.SessionManager;
import org.example.quizizz.repository.UserRepository;
import org.example.quizizz.security.JwtUtil;
import org.example.quizizz.service.Interface.IGameService;
import org.example.quizizz.service.Interface.IRoomService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Getter
public class SocketIOEventHandler {

    private final SocketIOServer socketIOServer;
    private final JwtUtil jwtUtil;
    private final IRoomService roomService;
    private final IGameService gameService;
    private final SessionManager sessionManager;
    private final ConnectionHandler connectionHandler;
    private final EventRegistrar eventRegistrar;
    private final UserRepository userRepository;
    private final RoomListEventHandler roomListEventHandler;

    @PostConstruct
    public void init() {
        connectionHandler.registerListeners(socketIOServer);
        eventRegistrar.registerEvents(socketIOServer, this);
        registerRoomEvents(socketIOServer);
        log.info("Socket.IO server initialized successfully");
    }

    private void registerRoomEvents(SocketIOServer server) {
        RoomEventHandler roomEventHandler = new RoomEventHandler(roomService, sessionManager, roomService.getRoomRepository());
        roomEventHandler.registerEvents(server);
        
        roomListEventHandler.registerEvents(server);
    }
}
