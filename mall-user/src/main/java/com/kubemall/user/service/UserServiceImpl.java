package com.kubemall.user.service;

import com.kubemall.user.entity.User;
import com.kubemall.user.repository.UserRepository;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull User createUser(@NonNull User user) {
        return Objects.requireNonNull(userRepository.save(user), "保存用户返回 null");
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}