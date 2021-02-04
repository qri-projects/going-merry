package com.ggemo.va.goingmerry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import com.ggemo.va.goingmerry.annotation.OpProxy;

public class OpProxyBeanPostProcessor implements InstantiationAwareBeanPostProcessor,
        ApplicationContextAware, BeanFactoryAware, PriorityOrdered {
    BeanFactory beanFactory;
    ApplicationContext applicationContext;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * <p>往bean中注入的时候 处理OpProxy的fields
     *
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        Class<?> clazz = bean.getClass();
        List<Field> fields = getAllFieldsList(clazz);
        for (Field field : fields) {
            OpProxy opProxy = field.getAnnotation(OpProxy.class);
            if (opProxy != null) {
                try {
                    Object proxyBean = findProxyBean(field.getType(), opProxy);
                    ReflectionUtils.makeAccessible(field);
                    field.set(bean, proxyBean);
                } catch (Exception e) {
                    throw new BeanCreationException(beanName, e);
                }
            }
        }

        return pvs;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return false;
    }

    private String proxyBeanName(OpProxy opProxy, Class<?> proxyType) {
        return "OpProxyBean-" + proxyType.getName();
    }

    private Object findProxyBean(Class<?> rpcProxyClass, OpProxy opProxy) {
        try {
            String proxyBeanFactoryName = "&" + proxyBeanName(opProxy, rpcProxyClass);
            OpProxyFactoryBean proxyBean = beanFactory.getBean(proxyBeanFactoryName, OpProxyFactoryBean.class);
            return proxyBean.getObject();
        } catch (NoSuchBeanDefinitionException ex) {
            return constructProxyBean(rpcProxyClass, opProxy);
        }
    }

    private Object constructProxyBean(Class<?> proxyType, OpProxy opProxy) throws Exception {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(OpProxyFactoryBean.class);

    }

    private static List<Field> getAllFieldsList(Class<?> cls) {
        Validate.isTrue(cls != null, "The class must not be null", new Object[0]);
        List<Field> allFields = new ArrayList<>();

        for (Class currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
        }

        return allFields;
    }
}
