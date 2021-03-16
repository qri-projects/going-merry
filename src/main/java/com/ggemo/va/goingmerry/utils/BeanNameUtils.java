package com.ggemo.va.goingmerry.utils;

public class BeanNameUtils {
    public static String generateBeanName(Class<?> i) {
        return i.getSimpleName() + "$GmProxyBean$" + i.getName().hashCode();
    }
}
