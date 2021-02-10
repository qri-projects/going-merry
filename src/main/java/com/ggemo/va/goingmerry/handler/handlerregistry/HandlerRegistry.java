package com.ggemo.va.goingmerry.handler.handlerregistry;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.handler.OpHandler;

public interface HandlerRegistry<AnalyseResult extends ConditionAnalyseResult> {
    void register(AnalyseResult analyseResult, OpHandler<?, ?> handler);

    OpHandler<?, ?> findHandler(AnalyseResult analyseResult);

    void initRegister(Class<? extends OpHandler<?, ?>> handlerClazz);

    boolean registered(Class<? extends OpHandler<?, ?>> handlerClazz);
}
