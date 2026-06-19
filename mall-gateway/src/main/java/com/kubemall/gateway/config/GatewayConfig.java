package com.kubemall.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kubemall.gateway.filter.JwtAuthFilter;

@Configuration
public class GatewayConfig {

        private final JwtAuthFilter jwtAuthenticationFilter;

        public GatewayConfig(JwtAuthFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("product-service", r -> r
                                                .path("/api/v1/products/**")
                                                .filters(f -> f
                                                                .filter(jwtAuthenticationFilter)
                                                                .stripPrefix(2))
                                                .uri("http://localhost:8082"))
                                .route("user-service", r -> r
                                                .path("/api/v1/**")
                                                .filters(f -> f
                                                                .filter(jwtAuthenticationFilter)
                                                                .stripPrefix(2))
                                                .uri("http://localhost:8081"))
                                .build();
        }
}
