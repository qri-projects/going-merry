package com.ggemo.va.goingmerry.handler.handleranalyse;

public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult, Condition> {
    AnalyseResult analyse(Condition condition);
}
