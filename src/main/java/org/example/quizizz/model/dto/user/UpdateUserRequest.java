package org.example.quizizz.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String password;
    
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;
    
    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String phoneNumber;
    
    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String address;
    
    private LocalDate dob;
    
    private String avatarURL;
    
    private String typeAccount;
    
    private Boolean emailVerified;
    
    private Long roleId;
}