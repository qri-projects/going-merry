package com.ggemo.va.goingmerry.handler.handleranalyse.impl;

import java.util.HashMap;
import java.util.Map;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.handler.OpHandler;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * String: like "package.path.ClassName#fieldName"<br/>
 * Object: field value
 */
@Getter
@Setter
public class ClassicConditionAnalyseResult extends HashMap<Object, Object> implements ConditionAnalyseResult {

    Class<? extends OpHandler<?, ?>> handlerClazz;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClassicConditionAnalyseResult)) {
            return false;
        }
        ClassicConditionAnalyseResult other = (ClassicConditionAnalyseResult) o;

        return super.equals(other)
                && other.getHandlerClazz() == this.getHandlerClazz();
    }
}
