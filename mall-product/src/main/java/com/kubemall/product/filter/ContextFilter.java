package com.kubemall.product.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String traceId = request.getHeader("X-Trace-Id");
        String username = request.getHeader("X-Username");
        String roles = request.getHeader("X-Roles");

        if (traceId != null) {
            MDC.put("traceId", traceId);
        }

        if (username != null) {
            MDC.put("username", username);
        }

        if (username != null && roles != null) {
            var authorities = Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
            SecurityContextHolder.clearContext();
        }
    }
}