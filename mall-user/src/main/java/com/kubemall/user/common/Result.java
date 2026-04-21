package com.kubemall.user.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    private String timestamp;
    
    public Result() {
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    // 成功 - 带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    // 成功 - 带消息和数据
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    // 成功 - 无数据
    public static <T> Result<T> success() {
        return success(null);
    }
    
    // 失败
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(-1);
        result.setMessage(message);
        return result;
    }
    
    // 失败 - 带业务码
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}