package org.example.quizizz.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class để tạo mật khẩu ngẫu nhiên an toàn
 */
@Component
public class PasswordGenerator {
    
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    private static final String ALL_CHARACTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS + NUMBERS;
    private static final int PASSWORD_LENGTH = 12;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Tạo mật khẩu ngẫu nhiên 12 ký tự
     * Đảm bảo có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt
     * 
     * @return mật khẩu ngẫu nhiên
     */
    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder();
        
        // Đảm bảo có ít nhất 1 ký tự từ mỗi loại
        password.append(getRandomCharacter(UPPERCASE_LETTERS));
        password.append(getRandomCharacter(LOWERCASE_LETTERS));
        password.append(getRandomCharacter(NUMBERS));

        // Điền các ký tự còn lại ngẫu nhiên
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(getRandomCharacter(ALL_CHARACTERS));
        }
        
        // Xáo trộn thứ tự các ký tự để tăng tính ngẫu nhiên
        return shuffleString(password.toString());
    }
    
    /**
     * Lấy ký tự ngẫu nhiên từ chuỗi cho trước
     */
    private char getRandomCharacter(String characters) {
        int randomIndex = secureRandom.nextInt(characters.length());
        return characters.charAt(randomIndex);
    }
    
    /**
     * Xáo trộn thứ tự các ký tự trong chuỗi
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int randomIndex = secureRandom.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        
        return new String(characters);
    }
}
