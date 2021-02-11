package com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl;

import java.util.HashMap;

import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.handler.OpHandler;

import lombok.Getter;
import lombok.Setter;

/**
 * String: like "package.path.ClassName#fieldName"<br/>
 * Object: field value
 */
@Getter
@Setter
public class ClassicConditionAnalyseResult extends HashMap<Object, Object> implements ConditionAnalyseResult {
}
