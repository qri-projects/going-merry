package com.ggemo.va.goingmerry.handlerregister;

import org.springframework.context.ApplicationContext;

import com.ggemo.va.handler.OpHandler;

public interface HandlerRegister {
    void initRegister(ApplicationContext applicationContext, Class<? extends OpHandler<?, ?>> handlerClazz);

    OpHandler<?, ?> findHandler(Object condition);
}
