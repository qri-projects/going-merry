package com.ggemo.va.goingmerry.handler;

import java.util.HashMap;
import java.util.Map;

import com.ggemo.va.step.OpStep;

public abstract class CacheStep<Context, Req, Res> implements OpStep<Context, Req, Res> {
    private Map<Req, Res> cache;

    public CacheStep() {
        this.cache = new HashMap<>();
    }

    protected abstract void setRes(Context context, Res res);
    protected abstract Req getReq(Context context);

    @Override
    public void handle(Context context) {
        setRes(context, cache.get(getReq(context)));
    }
}
