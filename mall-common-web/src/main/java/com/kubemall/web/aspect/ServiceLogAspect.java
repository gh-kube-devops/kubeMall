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

@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * Controller 层日志
     * 拦截所有子模块的 controller 包
     */
    @Around("execution(* com.kubemall..*.controller..*.*(..))")
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

        // Controller 前置日志
        log.info("=== [CONTROLLER] ===> {} {}", method, path);

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            
            // Controller 后置日志
            log.info("=== [CONTROLLER] <=== {} {} cost={}ms", method, path, cost);
            
            return result;

        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("=== [CONTROLLER] ERROR === {} {} cost={}ms", method, path, cost, e);
            throw e;
        }
    }

    /**
     * Service 层日志（核心业务）
     * 拦截所有子模块的 service 包
     */
    @Around("execution(* com.kubemall..*.service..*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName().replace("Impl", "");
        String methodName = joinPoint.getSignature().getName();
        String action = className + "." + methodName;

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            
            // Service 日志（成功）
            log.info("=== [SERVICE] === {} status=SUCCESS cost={}ms", action, cost);
            
            return result;

        } catch (BusinessException e) {
            long cost = System.currentTimeMillis() - start;
            
            // Service 日志（业务异常）
            log.warn("=== [SERVICE] === {} status=FAIL code={} msg={} cost={}ms", 
                     action, e.getCode(), e.getMessage(), cost);
            throw e;

        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            
            // Service 日志（系统异常）
            log.error("=== [SERVICE] === {} status=ERROR cost={}ms", action, cost, e);
            throw e;
        }
    }
}