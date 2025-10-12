package org.example.quizizz.common.constants;

public enum RoomEventType {
    // Room management events
    ROOM_CREATED("room_created"),
    ROOM_UPDATED("room_updated"),
    ROOM_DELETED("room_deleted"),

    // Player management events
    PLAYER_JOINED("player_joined"),
    PLAYER_LEFT("player_left"),
    HOST_TRANSFERRED("host_transferred"),

    // Game events
    GAME_STARTED("game_started"),
    GAME_ENDED("game_ended"),

    // Chat events
    CHAT_MESSAGE("chat_message"),

    // System events
    ROOM_SETTINGS_CHANGED("room_settings_changed"),
    PLAYER_KICKED("player_kicked");

    private final String eventName;

    RoomEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
