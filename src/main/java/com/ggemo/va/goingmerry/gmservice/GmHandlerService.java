package com.ggemo.va.goingmerry.gmservice;

import com.ggemo.va.handler.OpHandler;

public interface GmHandlerService<Condition, Req, Res> extends GmService<Condition>, OpHandler<Req, Res> {
}
