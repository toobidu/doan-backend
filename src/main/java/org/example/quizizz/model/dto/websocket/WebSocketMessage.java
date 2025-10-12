package org.example.quizizz.model.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {
    private String event;
    private T data;
    private LocalDateTime timestamp;
    private Boolean success = true;
    private String error;

    public static <T> WebSocketMessage<T> success(String event, T data) {
        WebSocketMessage<T> message = new WebSocketMessage<>();
        message.setEvent(event);
        message.setData(data);
        message.setTimestamp(LocalDateTime.now());
        message.setSuccess(true);
        return message;
    }

    public static <T> WebSocketMessage<T> error(String event, String error) {
        WebSocketMessage<T> message = new WebSocketMessage<>();
        message.setEvent(event);
        message.setError(error);
        message.setTimestamp(LocalDateTime.now());
        message.setSuccess(false);
        return message;
    }
}
