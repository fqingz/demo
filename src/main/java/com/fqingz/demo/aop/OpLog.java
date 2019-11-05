package com.fqingz.demo.aop;

import java.lang.annotation.*;

/**
 * @author Fang Qing
 * @date 2019/10/31 16:05
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention (RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {

    String value();
}
