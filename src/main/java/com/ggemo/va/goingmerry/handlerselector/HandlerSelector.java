package com.ggemo.va.goingmerry.handlerselector;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.impl.ClassicHandlerSelector;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>根据条件找实现类的接口
 *
 * @see ClassicHandlerSelector
 */
public interface HandlerSelector {
    /**
     * 根据条件找实现类
     * @param handlerClazz handler的类
     * @param mmCondition // todo: mmCondition文档  condition
     * @return {@link OpHandler}具体实现类
     */
    <Condition, HandlerReq, HandlerRes, S extends GmService<Condition, HandlerReq, HandlerRes>> S select(
            Class<S> handlerClazz, Condition mmCondition);
}
