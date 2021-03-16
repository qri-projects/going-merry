package com.ggemo.va.goingmerry.service.base;

import com.ggemo.va.handler.OpHandler;

public interface GmHandlerService<Condition, Req, Res> extends GmService<Condition>, OpHandler<Req, Res> {
}
