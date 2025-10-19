package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.authentication.*;
import org.example.quizizz.service.Interface.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "APIs liên quan đến đăng nhập, đăng ký")
public class AuthController {

    private final IAuthService authService;

    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản mới cho người dùng")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.USER_CREATED, response));
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập hệ thống")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_LOGIN_SUCCESS, response));
    }

    @Operation(summary = "Đăng xuất", description = "Đăng xuất khỏi hệ thống")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_LOGOUT_SUCCESS, "Logout successful"));
    }

    @Operation(summary = "Làm mới token", description = "Lấy access token mới từ refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_TOKEN_REFRESHED, response));
    }

    @Operation(summary = "Reset mật khẩu", description = "Reset mật khẩu người dùng qua email và tự động đăng xuất tất cả thiết bị")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_PASSWORD_RESET_SUCCESS, response));
    }

    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu cho người dùng hiện tại")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        ChangePasswordResponse response = authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_PASSWORD_CHANGED, response));
    }

    @Operation(summary = "Xác thực email", description = "Xác thực email sau khi đăng ký")
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        boolean verified = authService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_EMAIL_VERIFIED, "Email verified successfully"));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Quên mật khẩu", description = "Gửi mật khẩu mới qua email")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> forgotPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageCode.AUTH_PASSWORD_RESET_SUCCESS, response));
    }
}
