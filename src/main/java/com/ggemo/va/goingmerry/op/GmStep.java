package com.ggemo.va.goingmerry.op;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.BaseOpStep;

public abstract class GmStep<Context, Condition, Req, Res>
        extends BaseOpStep<Context, Req, Res> {

    protected Class<? extends OpHandler<Req, Res>> handlerClazz;

    protected abstract Collection<Condition> generateMmConditions(Context context);

    protected abstract OpHandler<Req, Res> selectHandler(Collection<Condition> conditions);

    protected abstract ApplicationContext getApplicationContext();

    @Override
    public void handle(Context context) {
        Collection<Condition> condition = generateMmConditions(context);
        Req req = generateReq(context);
        OpHandler<Req, Res> handler = selectHandler(condition);
        Res res = handler.handle(req);
        applyRes(context, res);
    }
}
