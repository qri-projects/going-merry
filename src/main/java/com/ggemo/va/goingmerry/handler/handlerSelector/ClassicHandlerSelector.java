package com.ggemo.va.goingmerry.handler.handlerSelector;

import java.util.HashMap;
import java.util.Map;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicReflectConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handlerregistry.ClassicHandlerRegistry;
import com.ggemo.va.goingmerry.handler.handlerregistry.HandlerRegistry;
import com.ggemo.va.handler.OpHandler;

public class ClassicHandlerSelector
        extends RegistryBasedHandlerSelector<ClassicConditionAnalyseResult> implements HandlerSelector {
    private static final Map<Object, OpHandler<?, ?>> CACHE = new HashMap<>();

    private static ClassicHandlerSelector INSTANCE = null;

    public static ClassicHandlerSelector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicHandlerSelector();
        }
        return INSTANCE;
    }

    private ClassicHandlerSelector() {
    }

    @Override
    protected HandlerRegistry<ClassicConditionAnalyseResult> getHandlerRegistry() {
        return ClassicHandlerRegistry.getInstance();
    }

    @Override
    protected ConditionAnalyzer<ClassicConditionAnalyseResult> getConditionAnalyzer() {
        return ClassicReflectConditionAnalyzer.getInstance();
    }

    @Override
    public OpHandler<?, ?> select(Class<? extends OpHandler<?, ?>> handlerClazz, Object mmCondition) {
        if (CACHE.containsKey(mmCondition)) {
            OpHandler<?, ?> inCacheHandler = CACHE.get(mmCondition);
            if (inCacheHandler == null) {
                throw new RuntimeException("// todo: 没有匹配的handler");
            } else {
                return inCacheHandler;
            }
        }
        OpHandler<?, ?> handler = selectWithOutCache(handlerClazz, mmCondition);
        CACHE.put(mmCondition, handler);
        return handler;
    }
}
