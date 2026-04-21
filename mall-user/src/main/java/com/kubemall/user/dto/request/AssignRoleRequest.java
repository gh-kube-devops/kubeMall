package com.kubemall.user.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AssignRoleRequest {
    private List<String> roles;
}