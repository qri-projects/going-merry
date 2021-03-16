package com.ggemo.va.goingmerry.aotuwired.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmAutowirable {
    @AliasFor("conditionGenerator")
    String value();

    /**
     * conditionGenerator的beanName
     */
    @AliasFor("value")
    String conditionGenerator();
}
