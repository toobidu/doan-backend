package org.example.quizizz.service.Interface;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    String uploadAvatar(MultipartFile file, Long userId) throws Exception;
    String uploadQuizImage(MultipartFile file, Long quizId) throws Exception;
    void deleteFile(String bucketName, String fileName) throws Exception;
    String getAvatarUrl(String fileName) throws Exception;
}
