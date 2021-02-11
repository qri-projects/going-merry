package com.ggemo.va.goingmerry.handler.handlerSelector;

import com.ggemo.va.handler.OpHandler;

public interface HandlerSelector {
    OpHandler<?, ?> select(Class<? extends OpHandler<?, ?>> handlerClazz, Object mmCondition);
}
