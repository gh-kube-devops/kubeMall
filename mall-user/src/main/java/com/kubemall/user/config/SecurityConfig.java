package com.kubemall.user.config;

import com.kubemall.web.config.BaseSecurityConfig;
import com.kubemall.web.filter.GatewayUserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
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
        // User 服务的 URL 白名单策略
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .anyRequest().authenticated());
    }
}