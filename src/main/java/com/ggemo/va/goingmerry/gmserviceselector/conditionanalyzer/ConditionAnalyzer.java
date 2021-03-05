package com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer;

import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.impl.ClassicConditionAnalyzer;

/**
 * <p>解析condition的接口
 *
 * @see ClassicConditionAnalyzer
 */
public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition);
}
