package org.example.quizizz.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quizizz.controller.socketio.listener.RoomListEventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Lắng nghe các sự kiện liên quan đến phòng
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoomEventListener {
    
    private final RoomListEventHandler roomListEventHandler;
    
    @EventListener
    public void handleRoomEvent(RoomEvent event) {
        switch (event.getEventType()) {
            case ROOM_CREATED:
                roomListEventHandler.notifyRoomCreated(event.getData());
                break;
            case ROOM_UPDATED:
                roomListEventHandler.notifyRoomUpdated(event.getData());
                break;
            case ROOM_DELETED:
                roomListEventHandler.notifyRoomDeleted((Long) event.getData());
                break;
        }
    }
}