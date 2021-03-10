package com.ggemo.va.goingmerry.annotation;

import java.util.Set;

public interface GmService<Condition>{
    Set<Condition> getConditions();
}
