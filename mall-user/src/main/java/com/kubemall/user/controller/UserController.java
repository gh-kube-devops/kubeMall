package com.kubemall.user.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kubemall.core.model.Result;
import com.kubemall.user.dto.request.AssignRoleRequest;
import com.kubemall.user.dto.request.ChangePasswordRequest;
import com.kubemall.user.dto.request.UserUpdateRequest;
import com.kubemall.user.dto.response.UserResponse;
import com.kubemall.user.entity.User;
import com.kubemall.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /users - 获取所有用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserResponse>> listUsers() {
        List<UserResponse> users = userService.findAllUsers()
                .stream()
                .map(UserResponse::from)
                .toList();
        return Result.success(users);
    }

    /**
     * GET /users/{id} - 获取单个用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = UserResponse.from(userService.findById(id));
        return Result.success(user);
    }

    /**
     * PUT /users/{id} - 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse user = UserResponse.from(userService.updateUser(id, request));
        return Result.success(user);
    }

    /**
     * 修改当前登录用户的密码
     * PATCH /users/password
     */
    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // 从 Token 获取当前登录用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 根据用户名查用户
        User user = userService.findByUsername(username);

        // 修改密码
        userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());

        return Result.success("Password updated successfully", null);
    }

    /**
     * POST /users/{id}/roles - 分配角色（子资源）
     */
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserResponse> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request) {

        UserResponse user = UserResponse.from(userService.assignRoles(id, request.getRoles()));
        return Result.success(user);
    }

    /**
     * DELETE /users/{id} - 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("User deleted successfully", null);
    }
}