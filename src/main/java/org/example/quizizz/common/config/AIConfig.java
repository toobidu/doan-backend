package org.example.quizizz.common.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    /**
     * Tạo một ChatClient để gửi và nhận phản hồi từ API của Gemini Flash.
     *
     * @param builder Builder của Gemini Flash
     * @return ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}