package com.ggemo.va.goingmerry.op;

import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.AbstractOpStep;
import com.ggemo.va.step.OpStep;

public abstract class BaseConditionalStep<Context, Condition, Req, Res> extends AbstractOpStep<Context, Req, Res>
        implements OpStep<Context, Req, Res> {

    public BaseConditionalStep(OpHandler<Req, Res> handler) {
        super(handler);
    }

    public abstract Condition generateCondition();

    public abstract WithConditionReq<Condition, Req> aggregateConditionAndReq(Condition condition, Req req);

    @Override
    public void handle(Context context) {

    }
}
