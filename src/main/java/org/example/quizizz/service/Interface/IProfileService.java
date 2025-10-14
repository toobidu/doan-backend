package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.profile.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProfileService {
    UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception;
    UpdateProfileResponse getProfile(Long userId);
    String getAvatarUrl(Long userId) throws Exception;
    List<UserSearchResponse> searchUsers(String keyword);
    PublicProfileResponse getPublicProfile(String username) throws Exception;
    ProfileStatsResponse getProfileStats(Long userId);
}
