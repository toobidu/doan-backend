package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.user.CreateUserRequest;
import org.example.quizizz.model.dto.user.UpdateUserRequest;
import org.example.quizizz.model.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    Page<UserResponse> searchUsers(String keyword, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}