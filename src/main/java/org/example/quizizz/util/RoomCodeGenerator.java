package org.example.quizizz.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class để generate room code duy nhất
 * Room code có format: 8 ký tự gồm chữ cái in hoa và số
 */
@Component
public class RoomCodeGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();
    
    /**
     * Generate room code ngẫu nhiên
     * @return room code 8 ký tự
     */
    public String generateRoomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        
        return code.toString();
    }
}
