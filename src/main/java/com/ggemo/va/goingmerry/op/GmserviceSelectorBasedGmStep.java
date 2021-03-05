package com.ggemo.va.goingmerry.op;

import com.ggemo.va.goingmerry.gmservice.GmHandlerService;
import com.ggemo.va.goingmerry.gmserviceselector.GmServiceSelector;

/**
 * <p>GmStep相关设计见docs/gm-step-design.md
 */
public abstract class GmserviceSelectorBasedGmStep<Context, Condition, Req, Res>
        extends GmStep<Context, Condition, Req, Res>{
    public abstract GmServiceSelector getGmServiceSelector();

    @Override
    protected <Service extends GmHandlerService<Condition, Req, Res>> Service selectHandler(Class<Service> handlerClazz,
                                                                                   Condition condition) {
        return getGmServiceSelector().select(handlerClazz, condition);
    }
}
