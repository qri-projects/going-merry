package com.ggemo.va.goingmerry.service.selectservice.selector.impl;

import org.springframework.stereotype.Component;

import com.ggemo.va.goingmerry.service.selectservice.selector.FlexConditionGmServiceSelector;

@Component
public class ClassicFlexConditionGmServiceSelector extends ClassicGmServiceSelector implements
        FlexConditionGmServiceSelector {
    ThreadLocal<Object> currentCondition;

    public <Condition> void setCondition(Condition condition) {
        currentCondition.set(condition);
    }

    @Override
    public <Condition> Condition getCondition() {
        return (Condition) currentCondition.get();
    }
}
