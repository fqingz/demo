package com.fqingz.demo.aop;

import java.lang.reflect.Method;

/**
 * @author Fang Qing
 * @date 2019/10/31 16:25
 */
@OpLog ("test13241343453")
public class Test {

    public static void main(String[] args) {
        Class<Test> t = Test.class;
        System.out.println (t.isAnnotation ());
        System.out.println (t.isAnnotationPresent (OpLog.class) );
        OpLog op = t.getAnnotation (OpLog.class);
        System.out.println (op.value () );
    }
}
