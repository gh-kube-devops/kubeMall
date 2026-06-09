package com.kubemall.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;

public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String traceId = MDC.get("traceId");
        String username = MDC.get("username");

        log.info("➡ [{}] {} {} user={}", traceId, method, uri, username);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;

            log.info("⬅ [{}] {} {} cost={}ms status={}",
                    traceId, method, uri, cost, response.getStatus());
        }
    }
}