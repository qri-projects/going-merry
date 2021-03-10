package com.ggemo.va.goingmerry.annotation;

import com.ggemo.va.handler.OpHandler;

public interface GmHandlerService<Condition, Req, Res> extends GmService<Condition>, OpHandler<Req, Res> {
}
