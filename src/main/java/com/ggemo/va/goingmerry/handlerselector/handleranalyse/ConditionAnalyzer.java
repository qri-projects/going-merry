package com.ggemo.va.goingmerry.handlerselector.handleranalyse;

import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>解析condition的接口
 *
 * @see ClassicConditionAnalyzer
 */
public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition, Class<? extends OpHandler<?, ?>> handlerClazz);
}
