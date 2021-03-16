package com.ggemo.va.goingmerry.service.base;

import java.util.Set;

/**
 * <p>实现该接口来实现策略
 */
public interface GmService<Condition> {
    Set<Condition> getGgConditions();
}
