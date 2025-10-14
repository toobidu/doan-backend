package org.example.quizizz.common.constants;

import lombok.Getter;

@Getter
public enum RedisKeyPrefix {
    USER_PERMISSIONS("user:%s:permissions"),
    GAME_SESSION("game:%s:session"),
    GAME_PLAYERS("game:%s:players"),
    GAME_SCORES("game:%s:scores"),
    QUIZ_CACHE("quiz:%s"),
    TOKEN_BLACKLIST("token:blacklist:%s"),
    USERS_ONLINE("users:online"),
    USER_PROFILE("user:%s:profile");

    private final String pattern;

    RedisKeyPrefix(String pattern) {
        this.pattern = pattern;
    }

    public String format(Object... args) {
        return String.format(pattern, args);
    }

}
