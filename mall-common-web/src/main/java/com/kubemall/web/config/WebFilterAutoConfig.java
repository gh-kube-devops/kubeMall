package com.kubemall.web.config;

import com.kubemall.web.filter.GatewayUserFilter;
import com.kubemall.web.filter.RequestLogFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class WebFilterAutoConfig {

    // 1. 先把 GatewayUserFilter 注册为标准的 Spring Bean，供给 Spring Security 依赖注入
    @Bean
    public GatewayUserFilter gatewayUserFilterBean() {
        return new GatewayUserFilter();
    }

    // 2. 将上面生成的 Bean 注入进来，包装进组件注册器中
    @Bean
    public FilterRegistrationBean<GatewayUserFilter> gatewayUserFilter(GatewayUserFilter gatewayUserFilterBean) {
        FilterRegistrationBean<GatewayUserFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(gatewayUserFilterBean); // 使用 Spring 管理的 Filter Bean
        bean.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*");
        bean.setName("gatewayUserFilter");
        return bean;
    }

    // -------------------------------------------------------------

    // 3. 同样的道理，RequestLogFilter 也先注册为独立的 Bean
    @Bean
    public RequestLogFilter requestLogFilterBean() {
        return new RequestLogFilter();
    }

    // 4. 再包装进组件注册器中
    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter(RequestLogFilter requestLogFilterBean) {
        FilterRegistrationBean<RequestLogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(requestLogFilterBean);
        bean.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE + 1);
        bean.addUrlPatterns("/*");
        bean.setName("requestLogFilter");
        return bean;
    }
}