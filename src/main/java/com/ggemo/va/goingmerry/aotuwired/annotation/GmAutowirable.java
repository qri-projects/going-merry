package com.ggemo.va.goingmerry.aotuwired.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Primary;

import com.ggemo.va.goingmerry.aotuwired.conditiongenerator.MmConditionGenerator;
import com.ggemo.va.goingmerry.service.base.GmService;

/**
 * <p>这个注解是用在继承了{@link GmService}的接口上, 使接口调用具有隐式的策略的能力
 *
 * <p>被这个注解的接口, 在被Autowired的时候, 不会按照传统的方式去spring中取bean, 而是会取到一个代理bean,
 * 该代理bean的方法被调用时会根据{@link GmService}的{@link GmService#getGgConditions()}和该注解的{@link GmAutowirable#value()}
 * 指定的{@link MmConditionGenerator}来将方法调用分配到匹配到的bean
 *
 * <p>带有该注解的接口的实现bean不能用@{@link Primary}注解, 否则会和代理的bean冲突
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmAutowirable {
    /**
     * {@link MmConditionGenerator}的 beanName, 使用时要创建{@link MmConditionGenerator}的bean, 在这里指定bean的name
     */
    String value() default "";
}
