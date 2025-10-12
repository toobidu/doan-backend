package org.example.quizizz.service.helper;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/*** Event được publish khi hết thời gian trả lời câu hỏi ***/
@Getter
public class GameTimerEvent extends ApplicationEvent {
    
    private final Long roomId;
    
    public GameTimerEvent(Object source, Long roomId) {
        super(source);
        this.roomId = roomId;
    }
}