package com.kubemall.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gateway 用户上下文过滤器
 * 
 * @author kubeMall
 */
public class GatewayUserFilter extends OncePerRequestFilter {

    /** 请求头常量 */
    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLES = "X-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        // 1. 提取请求头
        String traceId = request.getHeader(HEADER_TRACE_ID);
        String username = request.getHeader(HEADER_USERNAME);
        String roles = request.getHeader(HEADER_ROLES);

        // 2. 如果没有 traceId，自动生成一个
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }

        // 3. 如果没有 username，设置为 anonymous
        if (username == null || username.isEmpty()) {
            username = "anonymous";
        }

        // 4. 设置 MDC（日志上下文）
        setMdcContext(traceId, username);

        // 5. 设置 Spring Security 上下文
        setSecurityContext(username, roles);

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    /**
     * 生成 traceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 设置 MDC 日志上下文
     */
    private void setMdcContext(String traceId, String username) {
        MDC.put("traceId", traceId);
        MDC.put("username", username);
    }

    /**
     * 设置 Spring Security 上下文
     */
    private void setSecurityContext(String username, String roles) {
        // 解析角色
        List<SimpleGrantedAuthority> authorities = parseRoles(roles);

        // 创建认证信息
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                authorities);

        // 设置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 解析角色字符串为 GrantedAuthority 列表
     */
    private List<SimpleGrantedAuthority> parseRoles(String roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .toList();
    }
}