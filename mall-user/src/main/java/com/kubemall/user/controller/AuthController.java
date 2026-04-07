package com.kubemall.user.controller;

import com.kubemall.user.common.Result;
import com.kubemall.user.dto.UserDto;
import com.kubemall.user.entity.User;
import com.kubemall.user.security.JwtUtil;
import com.kubemall.user.security.PasswordUtil;
import com.kubemall.user.service.UserService;

import java.util.Objects;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Result<UserDto> register(@Valid @RequestBody UserDto dto) {
        User user = Objects.requireNonNull(userService.createUser(dto.toUser()), "创建用户返回 null");
        return Result.success(UserDto.from(user));
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserDto dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null || !PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return Result.success(token);
    }
}