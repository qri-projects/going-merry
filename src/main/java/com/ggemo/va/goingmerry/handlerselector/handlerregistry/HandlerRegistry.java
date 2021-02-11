package com.ggemo.va.goingmerry.handlerselector.handlerregistry;

import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.handler.OpHandler;

public interface HandlerRegistry<AnalyseResult extends ConditionAnalyseResult> {
    void register(AnalyseResult analyseResult, OpHandler<?, ?> handler);

    OpHandler<?, ?> findHandler(AnalyseResult analyseResult, Class<? extends OpHandler<?, ?>> handlerClazz);

    void initRegister(Class<? extends OpHandler<?, ?>> handlerClazz);

    boolean registered(Class<? extends OpHandler<?, ?>> handlerClazz);
}