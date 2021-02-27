package com.ggemo.va.goingmerry.op;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.HandlerSelector;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>GmStep相关设计见docs/gm-step-design.md
 */
public abstract class HandlerSelectorBasedGmStep<Context, Condition, Req, Res>
        extends GmStep<Context, Condition, Req, Res>{
    public abstract HandlerSelector getHandlerSelector();

    @Override
    protected <H extends GmService<Condition, Req, Res>> H selectHandler(Class<H> handlerClazz, Condition condition) {
        return (H) getHandlerSelector().select(handlerClazz, condition);
    }
}
