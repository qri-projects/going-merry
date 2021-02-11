package com.ggemo.va.goingmerry.handler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicReflectConditionAnalyzer;
import com.ggemo.va.handler.OpHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ClassicAnalyseConditionHandler
        implements OpHandler<ClassicAnalyseConditionHandler.Req, ClassicConditionAnalyseResult>,
        ConditionAnalyzer<ClassicConditionAnalyseResult> {
    private static final ClassicAnalyseConditionHandler INSTANCE = new ClassicAnalyseConditionHandler();
    public static ClassicAnalyseConditionHandler getInstance() {
        return INSTANCE;
    }


    @Override
    public ClassicConditionAnalyseResult handle(Req req) {
        Object condition = req.getCondition();
        if (condition == null) {
            ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();
            result.put(ClassicReflectConditionAnalyzer.DEFAULT_FIELD, null);
            return result;
        }

        ClassicConditionAnalyseResult result = new ClassicConditionAnalyseResult();
        result.setHandlerClazz(req.getHandlerClazz());

        // 枚举值
        if (condition instanceof Enum<?>) {
            result.put(ClassicReflectConditionAnalyzer.DEFAULT_FIELD, condition);
            return result;
        }

        // map
        if (condition instanceof Map) {
            Map<?, ?> conditionMap = (Map<?, ?>) condition;
            conditionMap.forEach((field, value) -> {
                result.put(ClassicReflectConditionAnalyzer.FIELD_PREFIX + field, value);
            });
            result.putAll(conditionMap);
            return result;
        }

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
    static class Req {
        Object condition;
        Class<? extends OpHandler<?, ?>> handlerClazz;
    }
}
