package com.kubemall.web.config;

import com.kubemall.web.filter.GatewayUserFilter;
import com.kubemall.web.filter.RequestLogFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class WebFilterAutoConfig {

    // 1. 注册为 Spring Bean，仅供 Spring Security 链注入；禁止 Servlet 层重复注册
    @Bean
    public GatewayUserFilter gatewayUserFilterBean() {
        return new GatewayUserFilter();
    }

    @Bean
    public FilterRegistrationBean<GatewayUserFilter> disableGatewayUserFilterServletRegistration(
            GatewayUserFilter gatewayUserFilterBean) {
        FilterRegistrationBean<GatewayUserFilter> bean = new FilterRegistrationBean<>(gatewayUserFilterBean);
        bean.setEnabled(false);
        return bean;
    }

    // -------------------------------------------------------------

    // 2. 注册为 Spring Bean，仅供 Spring Security 链注入；禁止 Servlet 层重复注册
    @Bean
    public RequestLogFilter requestLogFilterBean() {
        return new RequestLogFilter();
    }

    @Bean
    public FilterRegistrationBean<RequestLogFilter> disableRequestLogFilterServletRegistration(
            RequestLogFilter requestLogFilterBean) {
        FilterRegistrationBean<RequestLogFilter> bean = new FilterRegistrationBean<>(requestLogFilterBean);
        bean.setEnabled(false);
        return bean;
    }
}