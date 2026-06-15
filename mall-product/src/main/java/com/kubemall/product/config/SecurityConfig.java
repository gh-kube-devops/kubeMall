package com.kubemall.product.config;

import com.kubemall.web.config.BaseSecurityConfig;
import com.kubemall.web.filter.GatewayUserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // 在各微服务本地开启方法级权限
public class SecurityConfig extends BaseSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, GatewayUserFilter gatewayUserFilter) throws Exception {
        return super.createFilterChain(http, gatewayUserFilter);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return super.createPasswordEncoder();
    }

    @Override
    protected void configureAuthorization(HttpSecurity http) throws Exception {
        // Product 全放行，权限全靠 Controller 上的 @PreAuthorize
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    }
}