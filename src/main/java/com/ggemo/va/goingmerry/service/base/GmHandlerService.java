package com.ggemo.va.goingmerry.service.base;

import com.ggemo.va.handler.OpHandler;

/**
 * 能和{@link com.ggemo.va.business.OpBusiness}配合的{@link GmService}
 * @param <Condition>
 * @param <Req>
 * @param <Res>
 */
public interface GmHandlerService<Condition, Req, Res> extends GmService<Condition>, OpHandler<Req, Res> {
}
