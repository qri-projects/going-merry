package com.ggemo.va.goingmerry.aotuwired.proxybuilder.impl;

import java.lang.reflect.Proxy;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.ggemo.va.goingmerry.aotuwired.annotation.GmAutowirable;
import com.ggemo.va.goingmerry.service.base.GmService;
import com.ggemo.va.goingmerry.aotuwired.conditiongenerator.MmConditionGenerator;
import com.ggemo.va.goingmerry.aotuwired.proxybuilder.AutowiredGmServiceProxyBuilder;
import com.ggemo.va.goingmerry.service.selectservice.selector.GmServiceSelector;
import com.ggemo.va.goingmerry.utils.ApplicationContextUtil;
import com.ggemo.va.goingmerry.utils.SuperClassUtils;
import com.google.common.collect.Sets;

@Component
public class JdkProxyAutowiredGmServiceProxyBuilderImpl implements AutowiredGmServiceProxyBuilder {
    private static final Set<String> NO_PROXY_METHODS = Sets.newHashSet(
            "hashCode",
            "equals",
            "toString"
    );

    @Autowired
    GmServiceSelector serviceSelector;

    @Override
    public <Con, S extends GmService<Con>> S build(Class<S> serviceClazz) {
        Set<Class<?>> serverClazzes = SuperClassUtils.getAllSupers(serviceClazz);
        serverClazzes.add(Ordered.class);
        Class[] serverClazzesArray = serverClazzes.stream()
                .filter(Class::isInterface)
                .toArray(Class[]::new);

        GmAutowirable gmAutowirable = serviceClazz.getAnnotation(GmAutowirable.class);

        MmConditionGenerator<Con> conditionGenerator =
                (MmConditionGenerator<Con>) ApplicationContextUtil.getApplicationContext().getBean(gmAutowirable.conditionGenerator());


        Object proxyBean = Proxy.newProxyInstance(serviceClazz.getClassLoader(), serverClazzesArray,
                (proxy, method, args) -> {
                    if (NO_PROXY_METHODS.contains(method.getName())) {
                        //  不代理 hashCode, equals, toString 方法
                        return method.invoke(this, args);
                    }
                    if ("getOrder".equals(method.getName())) {
                        return Ordered.HIGHEST_PRECEDENCE;
                    }
                    if ("getClass".equals(method.getName())) {
                        return serviceClazz;
                    }
                    S service = serviceSelector.select(serviceClazz,
                            conditionGenerator.generateMmCondition());
                    return ReflectionUtils.invokeMethod(method, service, args);
                }
        );
        return (S) proxyBean;
    }
}
