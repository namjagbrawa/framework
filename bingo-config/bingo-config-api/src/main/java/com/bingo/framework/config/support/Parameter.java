package com.bingo.framework.config.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter
 * 
 * @author william.liangf
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Parameter {

    String key() default "";
    
    boolean required() default false;
    
    boolean excluded() default false;

    boolean escaped() default false;
    
    boolean attribute() default false;

    boolean append() default false;
    
}