package com.ggemo.va.goingmerry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.ggemo.va.goingmerry.annotation.OpProxy;

public class OpProxyFactoryBean implements FactoryBean<Object>, InitializingBean {
    private Class<?> type;

    private Object proxy;

    private OpProxy annotationInfos;

    @Override
    public Object getObject() throws BeansException {
        if (proxy != null) {
            return proxy;
        }
        proxy = constructProxy();
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        proxy = constructProxy();
    }

    private <T> T constructProxy() {

    }
}
