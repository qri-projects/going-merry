package com.ggemo.va.goingmerry.service.selectservice.serviceregistry;

import com.ggemo.va.goingmerry.service.base.GmService;
import com.ggemo.va.goingmerry.service.selectservice.condition.analyseResult.ConditionAnalyseResult;

/**
 * <p>注册gmService的registry, 提供注册, 查找等功能
 * @param <AnalyseResult> 注册的{@link ConditionAnalyseResult}类型
 */
public interface GmServiceRegistry<AnalyseResult extends ConditionAnalyseResult> {
    void initRegister();

    void register(GmService<?> service, String beanName);

    <S extends GmService<?>> S findService(AnalyseResult analyseResult, Class<S> serviceClazz);
}
