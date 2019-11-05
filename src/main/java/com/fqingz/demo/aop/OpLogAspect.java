package com.fqingz.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Fang Qing
 * @date 2019/10/31 17:22
 */
@Aspect
@Component
@Slf4j
public class OpLogAspect {

    @Around(value = "@annotation(opLog)")
    public Object around(ProceedingJoinPoint point, OpLog opLog) throws Throwable {
        Object objReturn = point.proceed ( );
        log.info ("around:注解在方法上，类：{}，方法：{}，注解值：{}", point.getSignature ( ).getDeclaringTypeName ( ), point.getSignature ( ).getName ( ), opLog.value ( ));
        return objReturn;
    }

    @Around("@within(opLog)")
    public Object around2(ProceedingJoinPoint point, OpLog opLog) throws Throwable {
        log.info ("within:注解在类上，类：{}，方法：{}，注解值：{}", point.getSignature ( ).getDeclaringTypeName ( ), point.getSignature ( ).getName ( ), opLog.value ( ));
        log.info ("获取访问url{}",extractRequestUrl());
        return point.proceed ( );
    }

    /**
     * 获取访问URL
     */
    private String extractRequestUrl() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes ( );
        HttpServletRequest request = attributes.getRequest ( );

        return request.getRequestURL ( ).toString ( );
    }
}
