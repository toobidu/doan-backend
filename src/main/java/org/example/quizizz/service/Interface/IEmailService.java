package org.example.quizizz.service.Interface;

/**
 * Interface cho Email Service
 * Xử lý việc gửi email cho các tính năng khác nhau
 */
public interface IEmailService {

    /**
     * Gửi email reset password với mật khẩu mới
     *
     * @param toEmail email người nhận
     * @param username tên người dùng
     * @param newPassword mật khẩu mới
     * @return true nếu gửi thành công, false nếu thất bại
     */
    boolean sendPasswordResetEmail(String toEmail, String username, String newPassword);

    /**
     * Gửi email xác thực tài khoản khi đăng ký
     *
     * @param toEmail email người nhận
     * @param username tên người dùng
     * @param verificationToken token xác thực
     * @return true nếu gửi thành công, false nếu thất bại
     */
    boolean sendVerificationEmail(String toEmail, String username, String verificationToken);
}
