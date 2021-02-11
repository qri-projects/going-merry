package com.ggemo.va.goingmerry.handler.handleranalyse.impl;

import java.lang.reflect.Field;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;

public class ClassicReflectConditionAnalyzer implements ConditionAnalyzer<ClassicConditionAnalyseResult> {
    private static ClassicReflectConditionAnalyzer INSTANCE = null;
    public static ClassicReflectConditionAnalyzer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicReflectConditionAnalyzer();
        }
        return INSTANCE;
    }

    private ClassicReflectConditionAnalyzer() { }

    @Override
    public ClassicConditionAnalyseResult analyse(Object condition) {
        Class<?> clazz = condition.getClass();
        String className = clazz.getCanonicalName();
        Field[] fields = clazz.getDeclaredFields();
        ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();

        if(condition instanceof Enum) {
            String fieldName = className + "#" + "value";
            result.put(fieldName, condition);
            return result;
        }

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
