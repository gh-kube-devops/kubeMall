package com.kubemall.user.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        // 生成 TraceId（整个请求只有一个）
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            MDC.put("traceId", traceId);
        }

        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        // 获取当前用户
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            MDC.put("username", auth.getName());
        }

        long start = System.currentTimeMillis();

        try {
            log.info("➡ {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            long cost = System.currentTimeMillis() - start;
            log.info("⬅ {} {} cost={}ms", request.getMethod(), request.getRequestURI(), cost);
        } finally {
            MDC.clear();
        }
    }
}