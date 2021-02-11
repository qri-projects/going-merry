package com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.handler.OpHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>{@link ConditionAnalyzer}的classic实现
 *
 * <p>将condition解析为一个map
 */
public class ClassicConditionAnalyzer
        implements OpHandler<ClassicConditionAnalyzer.Req, ClassicConditionAnalyseResult>,
        ConditionAnalyzer<ClassicConditionAnalyseResult> {
    public static final String DEFAULT_FIELD = "value";

    private static final ClassicConditionAnalyzer INSTANCE = new ClassicConditionAnalyzer();
    public static ClassicConditionAnalyzer getInstance() {
        return INSTANCE;
    }


    @Override
    public ClassicConditionAnalyseResult handle(Req req) {
        Object condition = req.getCondition();
        ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();

        if (condition == null) {
            result.put(DEFAULT_FIELD, null);
            return result;
        }

        // 枚举值
        if (condition instanceof Enum) {
            result.put(DEFAULT_FIELD, condition);
            return result;
        }

        // map
        if (condition instanceof Map) {
            Map<?, ?> conditionMap = (Map<?, ?>) condition;
            conditionMap.forEach((field, value) -> result.put(field.toString(), value));
            result.putAll(conditionMap);
            return result;
        }

        // todo: 不完善
        // list set等collection
        if (condition instanceof Collection) {
            Collection<Object> conditionCollection = (Collection<Object>) condition;
            for (Object o : conditionCollection) {
                result.put(o, true);
            }
            return result;
        }

        // 值对象
        Class<?> conditionClazz = condition.getClass();
        Field[] fields = conditionClazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                result.put(fieldName, field.get(condition));
            } catch (IllegalAccessException ignored) {
            }

        }
        return result;
    }

    @Override
    public ClassicConditionAnalyseResult analyse(Object condition, Class<? extends OpHandler<?, ?>> handlerClazz) {
        return handle(new Req(condition, handlerClazz));
    }

    @Data
    @AllArgsConstructor
    public static class Req {
        Object condition;
        Class<? extends OpHandler<?, ?>> handlerClazz;
    }
}
