package com.kubemall.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubemall.core.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class GlobalGatewayErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalGatewayErrorHandler.class);
    private final ObjectMapper objectMapper;

    public GlobalGatewayErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var request = exchange.getRequest();
        var path = request.getURI().getPath();

        // 获取 traceId（优先从 MDC，其次从请求头）
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeaders().getFirst("X-Trace-Id");
            if (traceId == null || traceId.isBlank()) {
                traceId = "unknown";
            }
        }

        int code = 500;
        String message = "网关内部错误";

        if (ex instanceof ResponseStatusException rse) {
            code = rse.getStatusCode().value();

            // 自定义消息
            if (code == 404) {
                message = "接口不存在，请检查请求路径";
            } else if (code == 401) {
                message = "未登录或Token已过期";
            } else if (code == 403) {
                message = "权限不足";
            } else {
                message = rse.getReason() != null ? rse.getReason() : "请求错误";
            }
        } else {
            message = ex.getMessage() != null ? ex.getMessage() : "网关内部错误";
        }

        // 分级日志处理
        if (code >= 400 && code < 500) {
            log.warn("[{}] ✗ Request failed method={} path={} status={} reason={}",
                    traceId,
                    request.getMethod(),
                    path,
                    code,
                    message);
        } else if (code >= 500) {
            log.error("[{}] ✗ Server error method={} path={} status={} reason={}",
                    traceId,
                    request.getMethod(),
                    path,
                    code,
                    message,
                    ex);
        } else {
            log.info("[{}] ✓ Request completed method={} path={} status={}",
                    traceId,
                    request.getMethod(),
                    path,
                    code);
        }

        // 防止重复写响应
        if (exchange.getResponse().isCommitted()) {
            return Mono.empty();
        }

        return buildErrorResponse(exchange, traceId, code, message);
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, String traceId, int code, String message) {
        var response = exchange.getResponse();

        try {
            Result<Void> result = Result.fail(code, message);
            result.setTraceId(traceId);

            byte[] bytes = objectMapper.writeValueAsBytes(result);

            response.setStatusCode(HttpStatus.valueOf(code));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {
            log.error("[{}] error_response_write_failed", traceId, e);
            return response.setComplete();
        }
    }
}