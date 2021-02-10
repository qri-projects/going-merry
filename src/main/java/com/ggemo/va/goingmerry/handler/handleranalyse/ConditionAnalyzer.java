package com.ggemo.va.goingmerry.handler.handleranalyse;

public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition);
}
