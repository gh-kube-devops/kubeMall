package com.kubemall.user.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class LoginResponse {
    
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private String expiresAt;
}