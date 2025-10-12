package org.example.quizizz.common.constants;

public enum MessageCode {
    SUCCESS("200", "Operation successful"),
    VALIDATION_ERROR("400", "Validation error"),
    INTERNAL_ERROR("500", "Internal server error"),
    UNAUTHORIZED("401", "Unauthorized access"),
    FORBIDDEN("403", "Access forbidden"),
    NOT_FOUND("404", "Resource not found"),
    BAD_REQUEST("400", "Bad request"),
    
    USER_CREATED("201", "User created successfully"),
    USER_NOT_FOUND("404", "User not found"),
    USER_ALREADY_EXISTS("409", "User already exists"),
    
    AUTH_LOGIN_SUCCESS("200", "Login successful"),
    AUTH_LOGOUT_SUCCESS("200", "Logout successful"),
    AUTH_TOKEN_REFRESHED("200", "Token refreshed successfully"),
    AUTH_PASSWORD_RESET_SUCCESS("200", "Password reset successfully"),
    AUTH_PASSWORD_CHANGED("200", "Password changed successfully"),
    AUTH_INVALID_TOKEN("401", "Invalid or expired token"),
    AUTH_PASSWORD_INCORRECT("401", "Incorrect password"),
    AUTH_PASSWORD_MISMATCH("400", "New password and confirm password do not match"),
    AUTH_EMAIL_SEND_FAILED("500", "Failed to send email"),
    AUTH_PASSWORD_RESET_FAILED("500", "Password reset failed"),
    
    PERMISSION_GRANTED("200", "Permission granted successfully"),
    PERMISSION_REVOKED("200", "Permission revoked successfully"),
    ROLE_ASSIGNED("200", "Role assigned successfully"),
    ROLE_REMOVED("200", "Role removed successfully"),
    ROLE_NOT_FOUND("404", "Role not found"),
    
    ROOM_NOT_FOUND("404", "Room not found"),
    ROOM_FULL("409", "Room is full"),
    ROOM_ALREADY_STARTED("409", "Room game already started"),
    ROOM_PERMISSION_DENIED("403", "Permission denied for this room action"),
    ROOM_ALREADY_JOINED("409", "User already joined this room"),
    ROOM_NOT_JOINED("400", "User has not joined this room"),
    ROOM_INVALID_MAX_PLAYERS("400", "Invalid maximum players for this mode"),
    
    GAME_ALREADY_STARTED("409", "Game has already started"),
    PLAYER_NOT_IN_GAME("400", "Player is not in the game"),
    
    FILE_UPLOADED("201", "File uploaded successfully"),
    AVATAR_UPDATED("200", "Avatar updated successfully"),
    AVATAR_URL_RETRIEVED("200", "Avatar URL retrieved successfully"),
    
    EMPTY_FILE("400", "File cannot be empty"),
    INVALID_FILE_TYPE("400", "Invalid file type"),
    FILE_TOO_LARGE("413", "File size too large"),
    INTERNAL_SERVER_ERROR("500", "Internal server error"),
    PERMISSION_NOT_FOUND("404", "Permission not found"),
    PERMISSION_ALREADY_EXISTS("409", "Permission already exists"),
    ROLE_ALREADY_EXISTS("409", "Role already exists"),
    
    ROOM_CREATED("201", "Room created successfully"),
    ROOM_UPDATED("200", "Room updated successfully"),
    ROOM_DELETED("200", "Room deleted successfully"),
    ROOM_JOINED("200", "Joined room successfully"),
    ROOM_LEFT("200", "Left room successfully"),
    ROOM_HOST_TRANSFERRED("200", "Host transferred successfully"),
    
    TOPIC_CREATED("201", "Topic created successfully"),
    TOPIC_UPDATED("200", "Topic updated successfully"),
    TOPIC_DELETED("200", "Topic deleted successfully"),
    
    GAME_STARTED("200", "Game started successfully"),
    PLAYER_LEFT_GAME("200", "Player left game successfully"),
    PLAYER_KICKED("200", "Player kicked successfully"),
    
    NOT_IMPLEMENTED("501", "Feature not implemented yet");

    private final String code;
    private final String message;

    MessageCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
