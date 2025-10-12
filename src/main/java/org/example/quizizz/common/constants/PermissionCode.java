package org.example.quizizz.common.constants;

public enum PermissionCode {
    USER_MANAGE("user:manage"),
    USER_MANAGE_PROFILE("user:manage_profile"),
    ROLE_MANAGE("role:manage"),
    PERMISSION_MANAGE("permission:manage"),
    QUESTION_MANAGE("question:manage"),
    TOPIC_MANAGE("topic:manage"),
    ROOM_MANAGE("room:manage"),
    ROOM_JOIN("room:join"),
    ROOM_KICK_PLAYER("room:kick_player"),
    ROOM_LEAVE("room:leave"),
    ROOM_INVITE("room:invite"),
    GAME_START("game:start"),
    GAME_ANSWER("game:answer"),
    GAME_VIEW_SCORE("game:view_score"),
    RANK_VIEW("rank:view");

    private final String code;

    PermissionCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
