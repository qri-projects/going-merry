package com.ggemo.va.goingmerry.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import com.ggemo.va.goingmerry.OpConditionWrapper;

@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface OpService {
    Class<? extends OpConditionWrapper<?>>[] value() default {};

    @AliasFor("value")
    Class<? extends OpConditionWrapper<?>>[] conditionClasses() default {};
}
