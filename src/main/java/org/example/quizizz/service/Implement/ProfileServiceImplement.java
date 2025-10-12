package org.example.quizizz.service.Implement;

import org.example.quizizz.mapper.ProfileMapper;
import org.example.quizizz.model.dto.profile.*;
import org.example.quizizz.model.entity.User;
import org.example.quizizz.repository.UserRepository;
import org.example.quizizz.service.Interface.IFileStorageService;
import org.example.quizizz.service.Interface.IProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service quản lý thông tin cá nhân người dùng (profile).
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImplement implements IProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final IFileStorageService fileStorageService;

    /**
     * Lấy thông tin profile của người dùng với presigned URL cho avatar.
     * @param userId Id người dùng
     * @return Thông tin profile
     */
    @Override
    public UpdateProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setDob(user.getDob());
        response.setCreatedAt(user.getCreatedAt());
        
        // Tạo presigned URL cho avatar
        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            try {
                response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
            } catch (Exception e) {
                response.setAvatarURL(null);
            }
        }
        
        return response;
    }

    /**
     * Cập nhật thông tin profile của người dùng.
     * @param userId Id người dùng
     * @param request Thông tin cập nhật
     * @return Thông tin profile sau cập nhật
     */
    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        profileMapper.updateUserFromDto(request, user);
        userRepository.save(user);
        
        return getProfile(userId); // Sử dụng lại method getProfile để có presigned URL
    }

    /**
     * Cập nhật avatar cho người dùng.
     * @param userId Id người dùng
     * @param file File avatar
     * @return Thông tin avatar mới
     * @throws Exception Nếu upload lỗi
     */
    @Override
    @Transactional
    public UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        // Upload file mới và lưu tên file vào DB
        String fileName = fileStorageService.uploadAvatar(file, userId);
        user.setAvatarURL(fileName); // Lưu tên file thay vì URL
        userRepository.save(user);
        
        // Không cần cache vì presigned URL có thời hạn
        
        // Tạo presigned URL mới cho response
        String presignedUrl = fileStorageService.getAvatarUrl(fileName);
        UpdateAvatarResponse response = new UpdateAvatarResponse();
        response.setAvatarURL(presignedUrl);
        return response;
    }

    /**
     * Lấy URL avatar của người dùng.
     * @param userId Id người dùng
     * @return Avatar URL
     * @throws Exception Nếu lỗi
     */
    @Override
    public String getAvatarUrl(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        String avatarURL = user.getAvatarURL();
        if (avatarURL == null || avatarURL.isEmpty()) {
            return null;
        }
        
        // Tạo presigned URL mới để đảm bảo không hết hạn
        String fileName = extractFileNameFromUrl(avatarURL);
        return fileStorageService.getAvatarUrl(fileName);
    }
    
    private String extractFileNameFromUrl(String url) {
        if (url == null) return null;
        // Extract filename from MinIO URL (before query parameters)
        String[] parts = url.split("/");
        String fileNameWithParams = parts[parts.length - 1];
        return fileNameWithParams.split("\\?")[0]; // Remove query parameters
    }
    
    @Override
    public List<UserSearchResponse> searchUsers(String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
            .limit(10) // Giới hạn 10 kết quả
            .map(user -> {
                UserSearchResponse response = new UserSearchResponse();
                response.setId(user.getId());
                response.setUsername(user.getUsername());
                response.setFullName(user.getFullName());
                
                // Tạo avatar URL nếu có
                if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
                    try {
                        response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
                    } catch (Exception e) {
                        response.setAvatarURL(null);
                    }
                }
                
                return response;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public PublicProfileResponse getPublicProfile(String username) throws Exception {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        
        PublicProfileResponse response = new PublicProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setDob(user.getDob());
        response.setCreatedAt(user.getCreatedAt());
        
        // Tạo avatar URL nếu có
        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            response.setAvatarURL(fileStorageService.getAvatarUrl(user.getAvatarURL()));
        }
        
        return response;
    }
}
