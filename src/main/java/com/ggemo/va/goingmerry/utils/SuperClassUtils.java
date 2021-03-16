package com.ggemo.va.goingmerry.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SuperClassUtils {
    public static <U> Set<Class<? extends U>> getServiceSuperClassesUntil(Class<? extends U> clazz,
                                                                          Class<U> until) {
        Set<Class<? extends U>> res = new HashSet<>();

        // 递归的出口
        if (clazz == null) {
            return res;
        }
        if (clazz.equals(until)) {
            res.add(until);
            return res;
        }

        // 遍历所有上面一层的类和接口
        for (Class<?> superClazz : getInterfacesAndSuperClass(clazz)) {
            Class<? extends U> sc = (Class<? extends U>) superClazz;
            // 递归地获取其所有继承自until的接口和父类
            Set<Class<? extends U>> superRes = getServiceSuperClassesUntil(sc, until);
            if (CollectionUtils.isEmpty(superRes)) {
                continue;
            }
            res.addAll(superRes);
        }

        // 不为空的话返回值加上自身
        if (!CollectionUtils.isEmpty(res)) {
            res.add(clazz);
        }
        return res;
    }

    /**
     * <p>工具方法, 找到给定类的接口和父类(一层)
     *
     * @see #getServiceSuperClassesUntil
     */
    private static Set<Class<?>> getInterfacesAndSuperClass(Class<?> clazz) {
        Set<Class<?>> res = Sets.newHashSet(clazz.getInterfaces().clone());
        res.add(clazz.getSuperclass());
        return res;
    }

    public static Set<Class<?>> getAllSupers(Class<?> c) {
        if (c == null) {
            return Sets.newHashSet();
        }

        Set<Class<?>> supers = getInterfacesAndSuperClass(c);

        if (CollectionUtils.isEmpty(supers)) {
            return supers;
        }

        Set<Class<?>> res = Sets.newHashSet();

        for (Class<?> sup : supers) {
            res.addAll(getAllSupers(sup));
        }
        res.addAll(supers);
        res.add(c);

        res = res.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return res;
    }
}
