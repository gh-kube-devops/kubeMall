package com.kubemall.product.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kubemall.product.exception.BusinessException;

@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * Controller 层日志
     */
    @Around("execution(* com.kubemall.product.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attrs.getRequest();

        String method = request.getMethod();
        String path = request.getRequestURI();

        // 请求日志
        log.info("http_request method={} path={}", method, path);

        try {
            Object result = joinPoint.proceed();

            long cost = System.currentTimeMillis() - start;

            // 响应日志
            log.info("http_response method={} path={} cost={}ms",
                    method, path, cost);

            return result;

        } catch (Exception e) {

            long cost = System.currentTimeMillis() - start;

            // 异常也打 response
            log.info("http_response method={} path={} cost={}ms",
                    method, path, cost);

            throw e;
        }
    }

    /**
     * Service 层日志（核心业务）
     */
    @Around("execution(* com.kubemall.product.service.*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName().replace("Impl", "");
        String methodName = joinPoint.getSignature().getName();
        String action = className + "." + methodName;

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            log.info("biz action={} status=SUCCESS cost={}ms",
                    action,
                    System.currentTimeMillis() - start);

            return result;

        } catch (BusinessException e) {

            log.warn("biz action={} status=FAIL",
                    action);

            throw e;

        } catch (Exception e) {

            log.error("biz action={} status=ERROR",
                    action, e);

            throw e;
        }
    }
}