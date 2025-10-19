package org.example.quizizz.controller.api;

import org.example.quizizz.common.config.ApiResponse;
import org.example.quizizz.common.constants.MessageCode;
import org.example.quizizz.model.dto.game.GameHistoryResponse;
import org.example.quizizz.model.dto.profile.PlayerStatsResponse;
import org.example.quizizz.model.dto.profile.UpdateAvatarResponse;
import org.example.quizizz.model.dto.profile.UpdateProfileRequest;
import org.example.quizizz.model.dto.profile.UpdateProfileResponse;
import org.example.quizizz.service.Interface.IProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Profile", description = "APIs liên quan đến hồ sơ người dùng")
public class ProfileController {

    private final IProfileService profileService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy hồ sơ người dùng", description = "Lấy thông tin hồ sơ của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> getProfile(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        UpdateProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Cập nhật hồ sơ người dùng", description = "Cập nhật thông tin hồ sơ của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfile(@RequestBody UpdateProfileRequest request, Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        UpdateProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Cập nhật hồ sơ người dùng theo ID", description = "Cập nhật thông tin hồ sơ của người dùng (chỉ được edit profile của chính mình)")
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfileById(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request, 
            Authentication auth) {
        Long currentUserId = Long.valueOf(auth.getName());
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error(403, MessageCode.FORBIDDEN, "Cannot edit other user's profile"));
        }
        UpdateProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Tải lên ảnh đại diện", description = "Tải lên ảnh đại diện cho người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateAvatarResponse>> updateAvatar(
            @Parameter(description = "File ảnh đại diện", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.EMPTY_FILE));
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.INVALID_FILE_TYPE));
            }
            
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.FILE_TOO_LARGE));
            }
            
            UpdateAvatarResponse response = profileService.updateAvatar(userId, file);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.AVATAR_UPDATED, response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi upload avatar: " + e.getMessage()));
        }
    }

    @GetMapping("/avatar")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy URL ảnh đại diện", description = "Lấy đường dẫn truy cập ảnh đại diện của người dùng hiện tại")
    public ResponseEntity<ApiResponse<String>> getAvatarUrl(Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            String avatarUrl = profileService.getAvatarUrl(userId);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.AVATAR_URL_RETRIEVED, avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi lấy avatar: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm người dùng", description = "Tìm kiếm người dùng theo username hoặc tên")
    public ResponseEntity<ApiResponse<java.util.List<org.example.quizizz.model.dto.profile.UserSearchResponse>>> searchUsers(
            @RequestParam String keyword) {
        java.util.List<org.example.quizizz.model.dto.profile.UserSearchResponse> users = profileService.searchUsers(keyword);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/public/{username}")
    @Operation(summary = "Xem hồ sơ công khai", description = "Xem thông tin công khai của người dùng khác")
    public ResponseEntity<ApiResponse<org.example.quizizz.model.dto.profile.PublicProfileResponse>> getPublicProfile(
            @PathVariable String username) {
        try {
            org.example.quizizz.model.dto.profile.PublicProfileResponse profile = profileService.getPublicProfile(username);
            return ResponseEntity.ok(ApiResponse.success(profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(404, MessageCode.USER_NOT_FOUND, "Không tìm thấy người dùng: " + username));
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy thống kê profile", description = "Lấy thống kê chi tiết của người dùng hiện tại để hiển thị trên Dashboard và ProfileStats")
    public ResponseEntity<ApiResponse<org.example.quizizz.model.dto.profile.ProfileStatsResponse>> getProfileStats(Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            org.example.quizizz.model.dto.profile.ProfileStatsResponse stats = profileService.getProfileStats(userId);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi lấy thống kê: " + e.getMessage()));
        }
    }

    @GetMapping("/game-history")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy lịch sử chơi game", description = "Lấy lịch sử 5 game gần nhất của người dùng")
    public ResponseEntity<ApiResponse<List<GameHistoryResponse>>> getGameHistory(Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            java.util.List<GameHistoryResponse> history = profileService.getRecentGameHistory(userId);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, history));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi lấy lịch sử: " + e.getMessage()));
        }
    }

    @GetMapping("/player-stats")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy thống kê người chơi", description = "Lấy thống kê và thành tích chi tiết của người chơi")
    public ResponseEntity<ApiResponse<PlayerStatsResponse>> getPlayerStats(Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            PlayerStatsResponse stats = profileService.getPlayerStats(userId);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.SUCCESS, stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi lấy thống kê: " + e.getMessage()));
        }
    }
}
