package com.ggemo.va.goingmerry.handler.handlerSelector;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handlerregister.HandlerRegistry;
import com.ggemo.va.handler.OpHandler;

public interface HandlerSelector<AnalyseResult extends ConditionAnalyseResult, Condition> {
    ConditionAnalyzer<AnalyseResult, Condition> getAnalyzer();

    HandlerRegistry<AnalyseResult> getRegistry();

    default AnalyseResult analyseCondition(Condition condition) {
        return getAnalyzer().analyse(condition);
    }

    default OpHandler<?, ?> selectByAnalyseResult(AnalyseResult analyseResult) {
        return getRegistry().findHandler(analyseResult);
    }

    default OpHandler<?, ?> select(Condition condition) {
        return selectByAnalyseResult(analyseCondition(condition));
    }

    OpHandler<?, ?> selectInCache(Condition condition);
}
