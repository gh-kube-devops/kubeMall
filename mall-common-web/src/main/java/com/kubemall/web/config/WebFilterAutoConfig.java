package com.kubemall.web.config;

import com.kubemall.web.filter.GatewayUserFilter;
import com.kubemall.web.filter.RequestLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterAutoConfig {

    @Bean
    public FilterRegistrationBean<GatewayUserFilter> gatewayUserFilter() {
        FilterRegistrationBean<GatewayUserFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new GatewayUserFilter());
        bean.setOrder(1);
        bean.addUrlPatterns("/*");
        bean.setName("gatewayUserFilter");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
        FilterRegistrationBean<RequestLogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestLogFilter());
        bean.setOrder(2);  // 在 GatewayUserFilter 之后执行
        bean.addUrlPatterns("/*");
        bean.setName("requestLogFilter");
        return bean;
    }
}