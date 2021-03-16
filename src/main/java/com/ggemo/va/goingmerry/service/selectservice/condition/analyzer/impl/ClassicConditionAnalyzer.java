package com.ggemo.va.goingmerry.service.selectservice.condition.analyzer.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ggemo.va.goingmerry.service.selectservice.condition.analyseResult.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.service.selectservice.condition.analyzer.ConditionAnalyzer;
import com.ggemo.va.handler.OpHandler;

/**
 * <p>{@link ConditionAnalyzer}的classic实现
 *
 * <p>将condition解析为一个{@link ClassicConditionAnalyseResult}
 */
@Component
public class ClassicConditionAnalyzer
        implements OpHandler<Object, ClassicConditionAnalyseResult>,
        ConditionAnalyzer<ClassicConditionAnalyseResult> {
    public static final String DEFAULT_FIELD = "value";

    @Override
    public ClassicConditionAnalyseResult analyse(Object condition) {
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
            result.putAll(conditionMap);
            return result;
        }

        // todo: 不完善
        // list等collection
        if (condition instanceof Collection) {
            Collection<?> conditionCollection = (Collection<?>) condition;

            if (condition instanceof HashSet) {
                HashSet<?> conditionSet = (HashSet<?>) condition;
                conditionSet.add(null);
            }


            for (Object o : conditionCollection) {
                result.put(o, true);
            }
            return result;
        }

        // 值对象
        Class<?> conditionClazz = condition.getClass();
        Field[] fields = conditionClazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                result.put(fieldName, field.get(condition));
            } catch (IllegalAccessException ignored) {
            }

        }
        return result;
    }

    /**
     * <p>提供给step的handle方法, 调用{@link ClassicConditionAnalyzer#analyse}方法
     *
     * @param condition condition
     * @return analyseResult
     */
    @Override
    public ClassicConditionAnalyseResult handle(Object condition) {
        return analyse(condition);
    }
}
