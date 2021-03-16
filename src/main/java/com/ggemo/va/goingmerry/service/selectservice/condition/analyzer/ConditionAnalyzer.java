package com.ggemo.va.goingmerry.service.selectservice.condition.analyzer;

import com.ggemo.va.goingmerry.service.selectservice.condition.analyseResult.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.service.selectservice.condition.analyzer.impl.ClassicConditionAnalyzer;

/**
 * <p>解析condition的接口
 *
 * @see ClassicConditionAnalyzer
 */
public interface ConditionAnalyzer<AnalyseResult extends ConditionAnalyseResult> {
    AnalyseResult analyse(Object condition);
}
