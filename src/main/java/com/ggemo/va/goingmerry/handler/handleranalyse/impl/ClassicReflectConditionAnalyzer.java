package com.ggemo.va.goingmerry.handler.handleranalyse.impl;

import java.lang.reflect.Field;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;

public class ClassicReflectConditionAnalyzer implements ConditionAnalyzer<ClassicConditionAnalyseResult, Object> {
    @Override
    public ClassicConditionAnalyseResult analyse(Object condition) {
        Class<?> clazz = condition.getClass();
        String className = clazz.getCanonicalName();
        Field[] fields = clazz.getDeclaredFields();
        ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();

        for (Field field : fields) {
            String fieldName = className + "#" + field.getName();
            try {
                result.put(fieldName, field.get(condition));
            } catch (IllegalAccessException ignored) {
            }

        }
        return result;
    }
}
