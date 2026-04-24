package com.kubemall.product.config;

import com.kubemall.product.filter.ContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContextFilter> contextFilter() {
        FilterRegistrationBean<ContextFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ContextFilter());
        bean.setOrder(1);
        return bean;
    }
}