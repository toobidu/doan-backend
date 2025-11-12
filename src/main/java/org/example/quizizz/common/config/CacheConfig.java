package org.example.quizizz.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình cache cho ứng dụng
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "roles", "role", 
            "permissions", "permission",
            "topics", "topic",
            "questions", "question", "questionsByTopic",
            "answers", "answer",
            "rooms", "room",
            "users", "user"
        );
    }
}
