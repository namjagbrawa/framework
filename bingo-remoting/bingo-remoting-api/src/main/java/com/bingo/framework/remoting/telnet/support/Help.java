package com.bingo.framework.remoting.telnet.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Help
 * 
 * @author william.liangf
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Help {

    String parameter() default "";

    String summary();

    String detail() default "";

}