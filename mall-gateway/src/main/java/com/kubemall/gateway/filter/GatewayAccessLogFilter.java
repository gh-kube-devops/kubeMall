package com.kubemall.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Integer.MIN_VALUE + 1)
public class GatewayAccessLogFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Trace-Id");

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        long start = System.currentTimeMillis();

        log.info("[{}] → {} {} start", traceId, method, path);

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long cost = System.currentTimeMillis() - start;
                    int status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 200;

                    log.info("[{}] ← {} {} status={} cost={}ms",
                            traceId, method, path, status, cost);
                });
    }
}