package com.ggemo.va.goingmerry.handler.handlerSelector;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handlerregistry.HandlerRegistry;
import com.ggemo.va.handler.OpHandler;

public abstract class RegistryBasedHandlerSelector<AnalyseResult extends ConditionAnalyseResult>
        implements HandlerSelector {

    protected abstract HandlerRegistry<AnalyseResult> getHandlerRegistry();

    protected abstract ConditionAnalyzer<AnalyseResult> getConditionAnalyzer();

    protected AnalyseResult analyseCondition(Object condition) {
//        return getConditionAnalyzer().analyse(condition);
        return null;
    }

    protected OpHandler<?, ?> selectWithOutCache
            (Class<? extends OpHandler<?, ?>> handlerClazz, Object mmCondition) {
        if (!getHandlerRegistry().registered(handlerClazz)) {
            getHandlerRegistry().initRegister(handlerClazz);
        }
        return getHandlerRegistry().findHandler(analyseCondition(mmCondition), handlerClazz);
    }
}
