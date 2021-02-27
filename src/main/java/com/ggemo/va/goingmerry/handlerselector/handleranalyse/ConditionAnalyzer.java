package com.ggemo.va.goingmerry.handlerselector.handleranalyse;

import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyzer;

/**
 * <p>解析condition的接口
 *
 * @see ClassicConditionAnalyzer
 */
public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition);
}
