package com.ggemo.va.goingmerry.gmservice;

import java.util.Set;

import com.ggemo.va.handler.OpHandler;

public interface GmService<Condition>{
    Set<Condition> getConditions();
}
