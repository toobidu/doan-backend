package org.example.quizizz.service.Implement;

import org.example.quizizz.service.Interface.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

/**
 * Implementation của Email Service
 * Sử dụng JavaMailSender và Thymeleaf template để gửi email
 */
@Service
public class EmailServiceImplement implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine emailTemplateEngine;
    private final String fromEmail;
    private final String companyName;
    private final String supportUrl;

    public EmailServiceImplement(JavaMailSender javaMailSender,
                                 TemplateEngine templateEngine,
                                 @Qualifier("fromEmail") String fromEmail,
                                 @Qualifier("companyName") String companyName,
                                 @Qualifier("supportUrl") String supportUrl) {
        this.javaMailSender = javaMailSender;
        this.emailTemplateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.companyName = companyName;
        this.supportUrl = supportUrl;
    }

    /**
     * Gửi email reset mật khẩu cho người dùng.
     * @param toEmail Email người nhận
     * @param username Tên người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu gửi thành công, false nếu lỗi
     */
    @Override
    public boolean sendPasswordResetEmail(String toEmail, String username, String newPassword) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Thiết lập thông tin email
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Mật Khẩu - " + companyName);

            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("newResetPassword", newPassword);
            context.setVariable("companyName", companyName);
            context.setVariable("supportUrl", supportUrl);
            context.setVariable("year", LocalDateTime.now().getYear());

            // Render HTML template
            String htmlContent = emailTemplateEngine.process("reset-password", context);
            helper.setText(htmlContent, true);

            // Gửi email
            javaMailSender.send(message);


            return true;

        } catch (MessagingException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gửi email xác thực tài khoản khi đăng ký.
     * @param toEmail Email người nhận
     * @param username Tên người dùng
     * @param verificationToken Token xác thực
     * @return true nếu gửi thành công, false nếu lỗi
     */
    @Override
    public boolean sendVerificationEmail(String toEmail, String username, String verificationToken) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản - " + companyName);

            // Tạo URL xác thực (frontend URL)
            String verificationUrl = "http://localhost:5173/verify-email?token=" + verificationToken;

            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("verificationUrl", verificationUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("supportUrl", supportUrl);
            context.setVariable("year", LocalDateTime.now().getYear());

            // Render HTML template
            String htmlContent = emailTemplateEngine.process("email-verification", context);
            helper.setText(htmlContent, true);

            // Gửi email
            javaMailSender.send(message);

            return true;

        } catch (MessagingException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
