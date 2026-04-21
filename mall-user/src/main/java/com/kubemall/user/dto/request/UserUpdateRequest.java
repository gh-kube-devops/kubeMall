package com.kubemall.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}