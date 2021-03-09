package com.ggemo.va.goingmerry.gmserviceselector;

import com.ggemo.va.goingmerry.gmservice.GmService;

public interface FlexConditionGmServiceSelector extends GmServiceSelector {
    default <Condition, S extends GmService<Condition>> S select(Class<S> gmServiceClazz) {
        return select(gmServiceClazz,getCondition());
    }

    <Condition> Condition getCondition();
}
