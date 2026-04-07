package com.kubemall.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                               JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf().disable() // 开发阶段关闭 CSRF，方便 Postman 测试
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/**").permitAll() // /auth/** 放行
                .anyRequest().authenticated()           // 其他接口需要认证
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                         UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}