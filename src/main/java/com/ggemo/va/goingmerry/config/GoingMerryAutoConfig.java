package com.ggemo.va.goingmerry.config;

import javax.annotation.Nonnull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ggemo.va.goingmerry.aotuwired.initializer.AutowirableServiceInitializer;
import com.ggemo.va.goingmerry.service.base.GmService;
import com.ggemo.va.goingmerry.service.selectservice.serviceregistry.GmServiceRegistry;
import com.ggemo.va.goingmerry.utils.ApplicationContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>使用时 可以继承一下这个类并加上{@code @Configure}注解进行配置
 */
@Configuration
@ConditionalOnClass({GmService.class})
@ComponentScan({"com.ggemo.va.goingmerry"})
@Slf4j
@Primary
public class GoingMerryAutoConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextUtil applicationContextUtil() {
        return new ApplicationContextUtil();
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext appC = ApplicationContextUtil.getApplicationContext();

        GmServiceRegistry<?> gmServiceRegistry =
                appC.getBean(GmServiceRegistry.class);

        log.info("Going Merry: ==start== init gmService register");
        gmServiceRegistry.initRegister();
        log.info("Going Merry: ===end=== init gmService register");

        AutowirableServiceInitializer autowirableServiceInitializer =
                appC.getBean(AutowirableServiceInitializer.class);

        log.info("Going Merry: ==start== register AutowiredGmServiceProxy");
        autowirableServiceInitializer.init();
        log.info("Going Merry: ===end=== register AutowiredGmServiceProxy");
    }
}
