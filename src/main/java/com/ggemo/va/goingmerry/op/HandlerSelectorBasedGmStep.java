package com.ggemo.va.goingmerry.op;

import java.util.Collection;

import com.ggemo.va.goingmerry.handler.handlerSelector.HandlerSelector;
import com.ggemo.va.handler.OpHandler;

public abstract class HandlerSelectorBasedGmStep<Context, Condition, Req, Res>
        extends GmStep<Context, Condition, Req, Res>{
    public abstract HandlerSelector getHandlerSelector();

    @Override
    protected OpHandler<Req, Res> selectHandler(Class<OpHandler<Req, Res>> handlerClazz, Condition condition) {
        return (OpHandler<Req, Res>) getHandlerSelector().select(handlerClazz, condition);
    }
}
