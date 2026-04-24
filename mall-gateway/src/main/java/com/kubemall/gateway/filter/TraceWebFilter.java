package com.kubemall.gateway.filter;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Integer.MIN_VALUE)
public class TraceWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }

        // 设置 MDC
        MDC.put("traceId", traceId);

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Trace-Id", traceId)
                .build();

        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signal -> MDC.clear());
    }
}