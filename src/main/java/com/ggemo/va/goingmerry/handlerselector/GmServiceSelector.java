package com.ggemo.va.goingmerry.handlerselector;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.impl.ClassicGmServiceSelector;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>根据条件找实现类的接口
 *
 * @see ClassicGmServiceSelector
 */
public interface GmServiceSelector {
    /**
     * 根据条件找实现类
     * @param handlerClazz handler的类
     * @param mmCondition // todo: mmCondition文档  condition
     * @return {@link OpHandler}具体实现类
     */
    <Condition, S extends GmService<Condition>> S select(
            Class<S> handlerClazz, Condition mmCondition);
}
