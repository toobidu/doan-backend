package org.example.quizizz.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RoomEvent extends ApplicationEvent {
    
    public enum Type {
        ROOM_CREATED,
        ROOM_UPDATED,
        ROOM_DELETED
    }
    
    private final Type eventType;
    private final Object data;
    
    public RoomEvent(Object source, Type eventType, Object data) {
        super(source);
        this.eventType = eventType;
        this.data = data;
    }
}