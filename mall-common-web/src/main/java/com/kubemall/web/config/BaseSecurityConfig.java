package com.kubemall.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubemall.core.model.Result;
import com.kubemall.web.filter.GatewayUserFilter;
import com.kubemall.web.filter.RequestLogFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public abstract class BaseSecurityConfig {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 提供给子类调用的通用过滤链构建逻辑（去掉了 @Bean）
     */
    public SecurityFilterChain createFilterChain(
            HttpSecurity http,
            GatewayUserFilter gatewayUserFilter,
            RequestLogFilter requestLogFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(this::handleAccessDenied)
                        .authenticationEntryPoint(this::handleAuthenticationEntryPoint))
                .addFilterBefore(
                        gatewayUserFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        requestLogFilter,
                        GatewayUserFilter.class);

        // 由子类实现具体的授权规则
        configureAuthorization(http);

        return http.build();
    }

    protected abstract void configureAuthorization(HttpSecurity http) throws Exception;

    /**
     * 提供给子类调用的通用加密器创建逻辑（去掉了 @Bean）
     */
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 403 权限不足
     */
    protected void handleAccessDenied(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.access.AccessDeniedException ex)
            throws java.io.IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Result<Void> result = Result.fail(403, "权限不足");
        result.setTraceId(getTraceId(request));

        response.getWriter()
                .write(objectMapper.writeValueAsString(result));
    }

    /**
     * 401 未登录
     */
    protected void handleAuthenticationEntryPoint(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException ex)
            throws java.io.IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Result<Void> result = Result.fail(401, "未登录或Token已过期");
        result.setTraceId(getTraceId(request));

        response.getWriter()
                .write(objectMapper.writeValueAsString(result));
    }

    /**
     * 获取链路追踪ID
     */
    protected String getTraceId(HttpServletRequest request) {

        Object requestTraceId = request.getAttribute("traceId");
        if (requestTraceId != null) {
            return requestTraceId.toString();
        }

        String mdcTraceId = MDC.get("traceId");
        if (mdcTraceId != null && !mdcTraceId.isBlank()) {
            return mdcTraceId;
        }

        String headerTraceId = request.getHeader("X-Trace-Id");
        if (headerTraceId != null && !headerTraceId.isBlank()) {
            return headerTraceId;
        }

        return "unknown";
    }
}