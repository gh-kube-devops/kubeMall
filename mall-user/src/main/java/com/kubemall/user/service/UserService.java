package com.kubemall.user.service;

import com.kubemall.user.dto.request.UserUpdateRequest;
import com.kubemall.user.entity.User;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建新用户
     * @param user 用户实体
     * @return 创建后的用户（包含ID）
     */
    @NonNull
    User createUser(@NonNull User user);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     * @throws RuntimeException 用户不存在时抛出
     */
    User findByUsername(String username);

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     * @throws RuntimeException 用户不存在时抛出
     */
    User findById(Long id);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param request 更新请求
     * @return 更新后的用户
     */
    User updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 分配角色
     * @param userId 用户ID
     * @param roleNames 角色名称列表
     * @return 分配角色后的用户
     */
    User assignRoles(Long userId, List<String> roleNames);
}