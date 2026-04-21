package com.kubemall.user.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    // Controller 层日志
    @Around("execution(* com.kubemall.user.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("▶ Controller: {}", methodName);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.debug("◀ Controller: {} cost={}ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("❌ Controller 异常: {}", methodName, e);
            throw e;
        }
    }

    // Service 层日志
    @Around("execution(* com.kubemall.user.service.*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("▶ Service: {}", methodName);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("◀ Service: {} cost={}ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("❌ Service 异常: {}", methodName, e);
            throw e;
        }
    }
}