package com.ggemo.va.goingmerry.conditionbeanspool;

import com.ggemo.va.handler.OpHandler;

public interface ConditionedBeansPool {
    void put(Class<?> clazz, Object condition, OpHandler<?, ?> bean);

    OpHandler<?, ?> get(Class<?> clazz, Object condition);

    boolean isConstructed(Class<?> clazz);

    void setConstructed(Class<?> clazz);
}
