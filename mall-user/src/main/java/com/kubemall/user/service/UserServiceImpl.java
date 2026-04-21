package com.kubemall.user.service;

import com.kubemall.user.dto.request.UserUpdateRequest;
import com.kubemall.user.entity.Role;
import com.kubemall.user.entity.User;
import com.kubemall.user.exception.BusinessException;
import com.kubemall.user.repository.RoleRepository;
import com.kubemall.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        
        // 1. 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw BusinessException.usernameAlreadyExists(user.getUsername());
        }
        
        // 2. 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 3. 获取默认角色
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> BusinessException.roleNotFound("ROLE_USER"));
        
        // 4. 设置默认角色
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(defaultRole));
        }
        
        User savedUser = userRepository.save(user);
        log.info("用户创建成功: ID={}, 用户名={}", savedUser.getId(), savedUser.getUsername());
        
        return savedUser;
    }

    @Override
    public User findByUsername(String username) {
        log.debug("根据用户名查询用户: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.userNotFound(username));
    }

    @Override
    public User findById(Long id) {
        log.debug("根据ID查询用户: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> BusinessException.userNotFound(id));
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        log.info("更新用户信息: ID={}", id);
        
        User user = findById(id);
        
        // 更新用户名
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // 检查新用户名是否已被其他用户使用
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw BusinessException.usernameAlreadyExists(request.getUsername());
                        }
                    });
            user.setUsername(request.getUsername());
        }
        
        // 更新邮箱
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: ID={}", id);
        
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.warn("删除用户: ID={}", id);
        
        if (!userRepository.existsById(id)) {
            throw BusinessException.userNotFound(id);
        }
        
        userRepository.deleteById(id);
        log.info("用户删除成功: ID={}", id);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("修改用户密码: ID={}", id);
        
        User user = findById(id);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw BusinessException.passwordIncorrect();
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("密码修改成功: ID={}", id);
    }

    @Override
    public List<User> findAllUsers() {
        log.debug("查询所有用户");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User assignRoles(Long userId, List<String> roleNames) {
        log.info("分配角色: userId={}, roles={}", userId, roleNames);
        
        User user = findById(userId);
        
        Set<Role> roles = roleNames.stream()
                .map(name -> {
                    String roleName = name.startsWith("ROLE_") ? name : "ROLE_" + name;
                    return roleRepository.findByName(roleName)
                            .orElseThrow(() -> BusinessException.roleNotFound(roleName));
                })
                .collect(Collectors.toSet());
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        
        log.info("角色分配成功: userId={}", userId);
        
        return savedUser;
    }
}