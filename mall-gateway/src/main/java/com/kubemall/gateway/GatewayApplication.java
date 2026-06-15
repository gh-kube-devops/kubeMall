package com.kubemall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("✅ 网关启动成功！端口: 8088");
    }

    @Bean
    @Order(-200)
    public GlobalFilter allRequestLogFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange);
        };
    }
}