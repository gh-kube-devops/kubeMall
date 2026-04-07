package com.kubemall.user.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    // 成功
    public static <T> Result<T> success(T data) {
        return new Result<T>(0, "success", data);
    }

    // 失败
    public static <T> Result<T> fail(String message) {
        return new Result<T>(-1, message, null);
    }
}