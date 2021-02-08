package com.ggemo.va.goingmerry.handlerregister;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import com.ggemo.va.goingmerry.GgConditionWrapper;
import com.ggemo.va.goingmerry.annotation.OpService;
import com.ggemo.va.handler.OpHandler;

public class ClassicHandlerRegister implements HandlerRegister{
    private static final Map<Object, OpHandler<?, ?>> CACHE = new HashMap<>();
    private static final Map<String, Set<OpHandler<?, ?>>> HANDLERS_HOLDER = new HashMap<>();

    private void registerHandler(String fieldKey, Object fieldValue, OpHandler<?, ?> handler) {
        String key = fieldKey + ":" + fieldValue.hashCode();
        Set<OpHandler<?, ?>> handlerSet;
        if (!HANDLERS_HOLDER.containsKey(key)) {
            handlerSet = new HashSet<>();
            HANDLERS_HOLDER.put(key, handlerSet);
        } else {
            handlerSet = HANDLERS_HOLDER.get(key);
        }
        handlerSet.add(handler);
    }

    @Override
    public void initRegister(ApplicationContext applicationContext, Class<? extends OpHandler<?, ?>> handlerClazz) {
        String[] beanNames = applicationContext.getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {
            OpService opService = applicationContext.findAnnotationOnBean(beanName, OpService.class);
            if (opService == null) {
                continue;
            }
            for (Class<? extends GgConditionWrapper<?>> wrapperClazz : opService.value()) {
                GgConditionWrapper<?> ggConditionWrapper = BeanUtils.instantiateClass(wrapperClazz);
                Object ggCondition = ggConditionWrapper.getGgCondition();

                Class<?> clazz = ggCondition.getClass();
                String className = clazz.getCanonicalName();

                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {
                    String fieldName = className + "#" + field.getName();
                    Object fieldValue = null;
                    try {
                        fieldValue = field.get(ggCondition);
                    } catch (IllegalAccessException ignored) {
                    }

                    registerHandler(fieldName, fieldValue,
                            applicationContext.getBean(beanName, handlerClazz));
                }
            }
        }
    }


    @Override
    public OpHandler<?, ?> findHandler(Object condition) {
        if (CACHE.containsKey(condition)) {
            return CACHE.get(condition);
        }

        Class<?> clazz = condition.getClass();
        String className = clazz.getCanonicalName();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = className + "#" + field.getName();
//            Object fieldValue = field.get(condition);
        }
        return null;
    }
}
