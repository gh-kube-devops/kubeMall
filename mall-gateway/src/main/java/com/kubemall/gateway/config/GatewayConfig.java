package com.kubemall.gateway.config;

import com.kubemall.gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户模块路由
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8080"))
                // 商品模块路由
                .route("product-service", r -> r
                        .path("/api/v1/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8081"))
                .build();
    }
}