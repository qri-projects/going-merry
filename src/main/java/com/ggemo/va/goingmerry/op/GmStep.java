package com.ggemo.va.goingmerry.op;

import org.springframework.context.ApplicationContext;

import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.BaseOpStep;

public abstract class GmStep<Context, Condition, Req, Res>
        extends BaseOpStep<Context, Req, Res> {

    protected Class<? extends OpHandler<Req, Res>> handlerClazz;

    protected abstract Condition generateMmCondition(Context context);

    protected abstract <H extends OpHandler<Req, Res>> H selectHandler(Class<H> handlerClazz, Condition condition);

    protected abstract ApplicationContext getApplicationContext();

    @Override
    public void handle(Context context) {
        Condition condition = generateMmCondition(context);
        Req req = generateReq(context);
        OpHandler<Req, Res> handler = selectHandler(handlerClazz, condition);
        Res res = handler.handle(req);
        applyRes(context, res);
    }
}
