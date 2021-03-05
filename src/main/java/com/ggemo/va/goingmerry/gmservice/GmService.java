package com.ggemo.va.goingmerry.gmservice;

import java.util.Set;

public interface GmService<Condition>{
    Set<Condition> getConditions();
}
