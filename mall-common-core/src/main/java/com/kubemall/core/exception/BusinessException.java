package com.kubemall.core.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final Integer code;

  public BusinessException(String message) {
    super(message);
    this.code = 400;
  }

  public BusinessException(Integer code, String message) {
    super(message);
    this.code = code;
  }

  public static BusinessException usernameAlreadyExists(String username) {
    return new BusinessException(409, "用户名已存在: " + username);
  }

  public static BusinessException userNotFound(Long id) {
    return new BusinessException(404, "用户不存在: ID=" + id);
  }

  public static BusinessException userNotFound(String username) {
    return new BusinessException(404, "用户不存在: " + username);
  }

  public static BusinessException passwordIncorrect() {
    return new BusinessException(401, "旧密码错误");
  }

  public static BusinessException roleNotFound(String roleName) {
    return new BusinessException(404, "角色不存在: " + roleName);
  }

  public static BusinessException loginFailed() {
    return new BusinessException(401, "用户名或密码错误");
  }

  public static BusinessException tokenExpired() {
    return new BusinessException(401, "Token已过期，请重新登录");
  }

  public static BusinessException tokenInvalid() {
    return new BusinessException(401, "Invalid token");
  }

  public static BusinessException accessDenied() {
    return new BusinessException(403, "权限不足");
  }

  public static BusinessException unauthorized() {
    return new BusinessException(401, "未登录或Token已过期");
  }
}
