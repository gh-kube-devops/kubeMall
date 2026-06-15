package com.kubemall.web.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.kubemall.core.exception.BusinessException;
import com.kubemall.core.model.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getTraceId() {
        return MDC.get("traceId");
    }

    /**
     * 创建带 traceId 的失败结果
     */
    private <T> Result<T> failResult(Integer code, String message) {
        Result<T> result = Result.fail(code, message);
        result.setTraceId(getTraceId());
        return result;
    }

    private <T> Result<T> failResult(Integer code, String message, T data) {
        Result<T> result = Result.fail(code, message);
        result.setTraceId(getTraceId());
        result.setData(data);
        return result;
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.info("BusinessError traceId={} code={} msg={}", getTraceId(), e.getCode(), e.getMessage());
        return failResult(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("ValidationError traceId={} errors={}", getTraceId(), errors);
        return failResult(400, "参数校验失败", errors);
    }

    /**
     * 方法级权限不足（@PreAuthorize）
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException ex) {
        log.warn("AccessDenied traceId={} msg={}", getTraceId(), ex.getMessage());
        return failResult(403, "权限不足");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("SysError traceId={}", getTraceId(), e);
        return failResult(500, "服务器内部错误");
    }
}