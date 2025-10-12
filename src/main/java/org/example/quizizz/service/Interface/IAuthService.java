package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.authentication.*;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse refreshToken(String refreshToken);
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
    ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request);
    void logoutAllDevices(Long userId);
}
