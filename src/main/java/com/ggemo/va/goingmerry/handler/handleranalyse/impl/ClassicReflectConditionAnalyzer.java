package com.ggemo.va.goingmerry.handler.handleranalyse.impl;

import java.lang.reflect.Field;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.handler.OpHandler;

public class ClassicReflectConditionAnalyzer implements ConditionAnalyzer<ClassicConditionAnalyseResult> {
    private static ClassicReflectConditionAnalyzer INSTANCE = null;
    public static final String FIELD_PREFIX = "#";
    public static final String DEFAULT_FIELD = "#value";

    public static ClassicReflectConditionAnalyzer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicReflectConditionAnalyzer();
        }
        return INSTANCE;
    }

    private ClassicReflectConditionAnalyzer() { }

    @Override
    public ClassicConditionAnalyseResult analyse(Object condition, Class<? extends OpHandler<?, ?>> handlerClazz) {
        Class<?> clazz = condition.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();

        if(condition instanceof Enum) {
            String fieldName = DEFAULT_FIELD;
            result.put(fieldName, condition);
            return result;
        }

        for (Field field : fields) {
            String fieldName =  FIELD_PREFIX + field.getName();
            try {
                result.put(fieldName, field.get(condition));
            } catch (IllegalAccessException ignored) {
            }

        }
        return result;
    }
}
