package com.ggemo.va.goingmerry.conditionbeanspool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.ggemo.va.handler.OpHandler;

public class HashMapConditionedBeansPool implements ConditionedBeansPool {
    private static final Map<Class<?>, Map<Object, OpHandler<?, ?>>> POOL = new HashMap<>();
    private static final Set<Class<?>> CLASS_CONSTRUCTED = new HashSet<>();

    private static final HashMapConditionedBeansPool INSTANCE = new HashMapConditionedBeansPool();

    @Override
    public void put(Class<?> clazz, Object condition, OpHandler<?, ?> bean) {
        Map<Object,  OpHandler<?, ?>> condition2BeanMap;
        if (POOL.containsKey(clazz)) {
            condition2BeanMap = POOL.get(clazz);
        } else {
            condition2BeanMap = new HashMap<>();
            POOL.put(clazz, condition2BeanMap);
        }

        condition2BeanMap.put(condition, bean);
    }

    @Override
    public OpHandler<?, ?> get(Class<?> clazz, Object condition) {
        return Optional.ofNullable(POOL.get(clazz))
                .map(m -> m.get(condition))
                .orElse(null);
    }

    @Override
    public boolean isConstructed(Class<?> clazz) {
        return CLASS_CONSTRUCTED.contains(clazz);
    }

    @Override
    public void setConstructed(Class<?> clazz) {
        CLASS_CONSTRUCTED.add(clazz);
    }

    private HashMapConditionedBeansPool() {}

    public static HashMapConditionedBeansPool getInstance() {
        return INSTANCE;
    }
}
