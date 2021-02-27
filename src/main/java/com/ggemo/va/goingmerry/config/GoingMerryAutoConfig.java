package com.ggemo.va.goingmerry.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.handlerregistry.HandlerRegistry;
import com.ggemo.va.goingmerry.utiils.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>使用时 可以继承一下这个类并加上{@code @Configure}注解进行配置
 */
@Configuration
@ConditionalOnClass({GmService.class, Object.class})
@ComponentScan({"com.ggemo.va.goingmerry"})
@Slf4j
public class GoingMerryAutoConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        HandlerRegistry<?> handlerRegistry =
                ApplicationContextUtil.getApplicationContext().getBean(HandlerRegistry.class);
        log.info("Going Merry: ==start== init handler register");
        handlerRegistry.initRegister();
        log.info("Going Merry: ===end=== init handler register");
    }
}
