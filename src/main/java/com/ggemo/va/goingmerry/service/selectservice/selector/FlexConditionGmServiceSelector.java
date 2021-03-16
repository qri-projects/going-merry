package com.ggemo.va.goingmerry.service.selectservice.selector;

import com.ggemo.va.goingmerry.service.base.GmService;

public interface FlexConditionGmServiceSelector extends GmServiceSelector {
    default <Condition, S extends GmService<Condition>> S select(Class<S> gmServiceClazz) {
        return select(gmServiceClazz,getCondition());
    }

    <Condition> Condition getCondition();
}
