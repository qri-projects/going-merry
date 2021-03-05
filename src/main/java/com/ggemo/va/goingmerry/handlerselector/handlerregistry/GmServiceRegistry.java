package com.ggemo.va.goingmerry.handlerselector.handlerregistry;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>注册handler的registry, 提供注册, 查找等功能
 * @param <AnalyseResult> 注册的{@link ConditionAnalyseResult}类型
 */
public interface GmServiceRegistry<AnalyseResult extends ConditionAnalyseResult> {
    void initRegister();

    void register(AnalyseResult analyseResult, GmService<?> service);

    <S extends GmService<?>> S findService(AnalyseResult analyseResult, Class<S> serviceClazz);
}
