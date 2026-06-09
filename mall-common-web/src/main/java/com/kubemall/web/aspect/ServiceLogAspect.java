package com.kubemall.web.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kubemall.core.exception.BusinessException;

/**
 * 服务层日志切面（通用）
 * 
 * <p>自动拦截所有 MVC 服务的 Controller 和 Service 层，记录请求日志和业务日志
 */
@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * Controller 层日志
     * 拦截所有子模块的 controller 包
     */
    @Around("execution(* com.kubemall.*.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        String method = "";
        String path = "";
        
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            method = request.getMethod();
            path = request.getRequestURI();
        }

        // 请求日志
        log.info("http_request method={} path={}", method, path);

        try {
            Object result = joinPoint.proceed();

            long cost = System.currentTimeMillis() - start;
            log.info("http_response method={} path={} cost={}ms", method, path, cost);

            return result;

        } catch (Exception e) {

            long cost = System.currentTimeMillis() - start;
            log.info("http_response method={} path={} cost={}ms", method, path, cost);

            throw e;
        }
    }

    /**
     * Service 层日志（核心业务）
     * 拦截所有子模块的 service 包
     */
    @Around("execution(* com.kubemall.*.service.*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName().replace("Impl", "");
        String methodName = joinPoint.getSignature().getName();
        String action = className + "." + methodName;

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            log.info("biz action={} status=SUCCESS cost={}ms", action, System.currentTimeMillis() - start);

            return result;

        } catch (BusinessException e) {

            log.warn("biz action={} status=FAIL code={} msg={}", action, e.getCode(), e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("biz action={} status=ERROR", action, e);
            throw e;
        }
    }
}