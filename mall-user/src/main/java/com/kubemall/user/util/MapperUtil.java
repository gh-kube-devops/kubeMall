package com.kubemall.user.util;

import com.kubemall.user.dto.UserRespDTO;
import com.kubemall.user.entity.User;

public class MapperUtil {
    public static UserRespDTO toUserRespDTO(User user) {
        UserRespDTO dto = new UserRespDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}