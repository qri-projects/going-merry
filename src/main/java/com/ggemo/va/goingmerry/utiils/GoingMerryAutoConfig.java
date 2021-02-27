package com.ggemo.va.goingmerry.utiils;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import com.ggemo.va.goingmerry.handlerselector.handlerregistry.HandlerRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>使用时 可以继承一下这个类并加上{@code @Configure}注解进行配置
 */
@Configuration
@Slf4j
public class GoingMerryAutoConfig implements ApplicationContextAware{
    private static ApplicationContext APPLICATION_CONTEXT;

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }

    @PostConstruct
    public void init(@Autowired HandlerRegistry<?> handlerRegistry) {
        log.info("Going Merry: ==start== init handler register");
        handlerRegistry.initRegister();
        log.info("Going Merry: ===end=== init handler register");
    }

}
