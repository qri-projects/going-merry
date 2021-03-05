package com.ggemo.va.goingmerry.op;

import com.ggemo.va.goingmerry.gmservice.GmHandlerService;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.GmServiceSelector;

/**
 * <p>GmStep相关设计见docs/gm-step-design.md
 */
public abstract class HandlerSelectorBasedGmStep<Context, Condition, Req, Res>
        extends GmStep<Context, Condition, Req, Res>{
    public abstract GmServiceSelector getHandlerSelector();

    @Override
    protected <Service extends GmHandlerService<Condition, Req, Res>> Service selectHandler(Class<Service> handlerClazz,
                                                                                   Condition condition) {
        return getHandlerSelector().select(handlerClazz, condition);
    }
}
