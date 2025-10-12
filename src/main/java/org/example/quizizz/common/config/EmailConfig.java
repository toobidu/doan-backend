package org.example.quizizz.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Cấu hình cho email: JavaMailSender, TemplateEngine riêng cho email, các thông tin liên quan.
 * Tránh xung đột với Spring Boot auto-configuration bằng cách sử dụng tên bean riêng biệt.
 */
@Configuration
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${app.company.name:QuizizZ}")
    private String companyName;

    @Value("${app.support.url:mailto:support@quizizz.com}")
    private String supportUrl;

    /**
     * Cấu hình JavaMailSender cho việc gửi email.
     * @return JavaMailSender được cấu hình đầy đủ
     */
    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

    /**
     * Email gửi đi (from email).
     * @return Email gửi đi
     */
    @Bean(name = "fromEmail")
    public String fromEmail() {
        return username;
    }

    /**
     * Tên công ty hiển thị trong email.
     * @return Tên công ty
     */
    @Bean(name = "companyName")
    public String companyName() {
        return companyName;
    }

    /**
     * URL hỗ trợ hiển thị trong email.
     * @return URL hỗ trợ
     */
    @Bean(name = "supportUrl")
    public String supportUrl() {
        return supportUrl;
    }
}
