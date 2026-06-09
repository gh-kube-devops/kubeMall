package com.kubemall.user.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.kubemall.core.model.Result;
import com.kubemall.user.dto.request.LoginRequest;
import com.kubemall.user.dto.request.RegisterRequest;
import com.kubemall.user.dto.response.LoginResponse;
import com.kubemall.user.dto.response.UserResponse;
import com.kubemall.user.entity.User;
import com.kubemall.user.exception.BusinessException;
import com.kubemall.user.security.JwtUtil;
import com.kubemall.user.security.PasswordUtil;
import com.kubemall.user.service.UserService;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword());
        newUser.setEmail(request.getEmail());

        User user = userService.createUser(newUser);
        return Result.success(UserResponse.from(user));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.findByUsername(request.getUsername());

            if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
                throw BusinessException.loginFailed();
            }

            List<String> roles = user.getRoles()
                    .stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(user.getUsername(), roles);

            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
            String expiresAtStr = expiresAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roles)
                    .expiresAt(expiresAtStr)
                    .build();

            return Result.success(response);

        } catch (BusinessException e) {
            // 统一返回模糊的错误信息，不暴露用户名是否存在
            return Result.fail(401, "用户名或密码错误");
        }
    }
}