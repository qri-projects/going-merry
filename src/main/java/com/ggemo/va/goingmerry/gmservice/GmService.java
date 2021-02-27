package com.ggemo.va.goingmerry.gmservice;

import java.util.List;

import com.ggemo.va.handler.OpHandler;

public interface GmService<Condition, Req, Res> extends OpHandler<Req, Res> {
    List<Condition> getConditions();
}
