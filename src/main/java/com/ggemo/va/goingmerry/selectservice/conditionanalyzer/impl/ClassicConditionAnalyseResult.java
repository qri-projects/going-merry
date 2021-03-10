package com.ggemo.va.goingmerry.selectservice.conditionanalyzer.impl;

import java.util.HashMap;

import com.ggemo.va.goingmerry.selectservice.conditionanalyzer.ConditionAnalyseResult;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>AnalyseResult的Classic实现, 其实就是个Map
 */
@Getter
@Setter
public class ClassicConditionAnalyseResult extends HashMap<Object, Object> implements ConditionAnalyseResult {
}
