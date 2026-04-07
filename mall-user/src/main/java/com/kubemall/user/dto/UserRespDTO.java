package com.kubemall.user.dto;

import lombok.Data;

@Data
public class UserRespDTO {
    private Long id;
    private String username;
    private String email;
}