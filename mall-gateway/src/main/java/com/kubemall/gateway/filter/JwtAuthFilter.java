package com.kubemall.gateway.filter;

import com.kubemall.gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final String TRACE_ID = "X-Trace-Id";
    private static final String USERNAME = "X-Username";
    private static final String ROLES = "X-Roles";

    private final JwtUtil jwtUtil;

    private static final List<String> WHITE_LIST = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register");

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // =========================
        // traceId
        // =========================
        String traceId = request.getHeaders().getFirst(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = java.util.UUID.randomUUID()
                    .toString()
                    .replace("-", "");
        }

        // =========================
        // 白名单
        // =========================
        if (WHITE_LIST.contains(path)) {

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(TRACE_ID, traceId)
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build());
        }

        // =========================
        // Token 校验
        // =========================
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[{}] Auth failed path={} reason=Missing token",
                    traceId, path);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "缺少Token");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.warn("[{}] Auth failed path={} reason=Invalid or expired token",
                    traceId, path);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token无效或已过期");
        }

        // =========================
        // 用户信息
        // =========================
        String username = jwtUtil.getUsername(token);
        List<String> roles = jwtUtil.getRoles(token);

        log.info("[{}] Auth success user={} roles={}",
                traceId, username, String.join(",", roles));

        // =========================
        // 透传用户信息
        // =========================
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TRACE_ID, traceId)
                .header(USERNAME, username)
                .header(ROLES, String.join(",", roles))
                .build();

        return chain.filter(
                exchange.mutate()
                        .request(mutatedRequest)
                        .build());
    }

    @Override
    public int getOrder() {
        return -50;
    }
}