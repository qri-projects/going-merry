package com.ggemo.va.goingmerry.gmservice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * <p>使用该注解声明一个Gm策略类
 *
 * <p>参数是一个{@link GgConditionWrapper}的数组, <br/>
 * 一个GmService可以提供给多个条件使用, 命中其中一个即为命中该GmService
 */
@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface GmService {
    Class<? extends GgConditionWrapper<?>>[] value() default {};
}
