package org.example.quizizz.common.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    /**
     * Cấu hình MinIO client.
     * @return MinIO client được cấu hình đầy đủ
     */
    @Bean
    public MinioClient minioClient() {
        try {
            log.info("Configuring MinIO client with endpoint: {}", endpoint);
            log.info("Using access key: {}", accessKey.substring(0, Math.min(4, accessKey.length())) + "***");

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    // Thêm region để tránh lỗi signature
                    .region("us-east-1")
                    .build();

            log.info("MinIO client configured successfully");
            return client;
        } catch (Exception e) {
            log.error("Failed to configure MinIO client: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO configuration failed", e);
        }
    }
}
