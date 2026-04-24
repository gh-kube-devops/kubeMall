package com.kubemall.product.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kubemall.product.common.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 只用于返回给前端
     */
    private String getTraceId() {
        return MDC.get("traceId");
    }

    /**
     * 未登录 / Token问题
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthException(AuthenticationException e) {
        log.warn("AuthError traceId={} msg={}", getTraceId(), e.getMessage());
        return Result.fail(401, "未登录或Token已过期");
    }

    /**
     * 权限不足
     */
    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(Exception e) {
        log.warn("AccessDenied traceId={} msg={}", getTraceId(), e.getMessage());
        return Result.fail(403, "权限不足");
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.info("BusinessError traceId={} code={} msg={}", getTraceId(), e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
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

        return Result.fail(400, "参数校验失败");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("SysError traceId={}", getTraceId(), e);
        return Result.fail(500, "服务器内部错误");
    }
}