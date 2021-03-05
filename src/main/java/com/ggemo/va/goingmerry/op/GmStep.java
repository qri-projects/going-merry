package com.ggemo.va.goingmerry.op;

import com.ggemo.va.goingmerry.gmservice.GmHandlerService;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.BaseOpStep;

/**
 * <p>GmStep相关设计见docs/gm-step-design.md
 */
public abstract class GmStep<Context, Condition, Req, Res>
        extends BaseOpStep<Context, Req, Res> {

    protected Class<? extends GmHandlerService<Condition, Req, Res>> handlerClazz;

    protected abstract Condition generateMmCondition(Context context);

    protected abstract <H extends GmHandlerService<Condition, Req, Res>> H selectHandler(Class<H> handlerClazz,
                                                                                         Condition condition);

    @Override
    public void handle(Context context) {
        Condition condition = generateMmCondition(context);
        Req req = generateReq(context);
        GmHandlerService<Condition, Req, Res> handler = selectHandler(handlerClazz, condition);
        Res res = handler.handle(req);
        applyRes(context, res);
    }
}
