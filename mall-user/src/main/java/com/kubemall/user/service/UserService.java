package com.kubemall.user.service;

import java.util.List;

import com.kubemall.user.entity.User;
import org.springframework.lang.NonNull;

public interface UserService {
    @NonNull
    User createUser(@NonNull User user);
    User findByUsername(String username);
    List<User> getAllUsers();
}