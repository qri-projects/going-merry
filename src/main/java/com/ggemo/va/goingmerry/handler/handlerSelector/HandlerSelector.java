package com.ggemo.va.goingmerry.handler.handlerSelector;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handlerregistry.HandlerRegistry;
import com.ggemo.va.handler.OpHandler;

public interface HandlerSelector {
    OpHandler<?, ?> select(Class<? extends OpHandler<?, ?>> handlerClazz, Object mmCondition);
}
