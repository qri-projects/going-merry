package com.ggemo.va.goingmerry.aotuwired.initializer.impl;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.ggemo.va.goingmerry.aotuwired.annotation.GmAutowirable;
import com.ggemo.va.goingmerry.aotuwired.initializer.AutowirableServiceInitializer;
import com.ggemo.va.goingmerry.aotuwired.proxybuilder.AutowiredGmServiceProxyBuilder;
import com.ggemo.va.goingmerry.service.base.GmService;
import com.ggemo.va.goingmerry.utils.ApplicationContextUtil;
import com.ggemo.va.goingmerry.utils.BeanNameUtils;
import com.ggemo.va.goingmerry.utils.SuperClassUtils;


@Component
public class ClassicAutowirableServiceInitializer implements AutowirableServiceInitializer {
    @Override
    public void init() {
        ApplicationContext appC = ApplicationContextUtil.getApplicationContext();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) appC.getAutowireCapableBeanFactory();
        AutowiredGmServiceProxyBuilder proxyBeanBuilder = appC.getBean(AutowiredGmServiceProxyBuilder.class);

        // 获取{@link GmService}的所有bean
        appC.getBeansOfType(GmService.class)
                .values()
                .stream()

                // 获得所有{@link GmService}的bean实现的接口, 找出被{@link GmAutowirable}注解的接口
                .flatMap(
                        b -> SuperClassUtils.getServiceSuperClassesUntil(b.getClass(), GmService.class)
                                .stream()
                                .filter(Class::isInterface)
                                .filter(c -> c.isAnnotationPresent(GmAutowirable.class))
                                .map(c -> (Class<GmService>) c)
                )
                .distinct()

                // 构建代理bean, 塞到spring里
                .forEach(i -> {
                    GmService<?> proxyBean = proxyBeanBuilder.build(i);

                    String beanName = BeanNameUtils.generateBeanName(i);

                    BeanDefinition beanDefinition = BeanDefinitionBuilder
                            .genericBeanDefinition(i, () -> proxyBean)
                            // 代理bean是primary的
                            .setPrimary(true)
                            .getBeanDefinition();
                    beanFactory.registerBeanDefinition(beanName, beanDefinition);
                });
    }
}
