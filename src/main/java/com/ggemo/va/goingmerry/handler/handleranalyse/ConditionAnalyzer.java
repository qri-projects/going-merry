package com.ggemo.va.goingmerry.handler.handleranalyse;

import com.ggemo.va.handler.OpHandler;

public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition, Class<? extends OpHandler<?, ?>> handlerClazz);
}
