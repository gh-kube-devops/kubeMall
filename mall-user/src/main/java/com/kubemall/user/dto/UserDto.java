package com.kubemall.user.dto;

import com.kubemall.user.entity.User;
import com.kubemall.user.security.PasswordUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 
    private String password;

    @Email(message = "邮箱格式错误")
    private String email;

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(PasswordUtil.encode(this.password));
        user.setEmail(this.email);
        return user;
    }

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // 不设置密码
        return dto;
    }
}