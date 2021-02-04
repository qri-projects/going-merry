package com.ggemo.va.goingmerry.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.ggemo.va.handler.OpHandler;

public class GetClassFieldsHandler implements OpHandler<Class<?>, List<Field>> {
    @Override
    public List<Field> handle(Class<?> cls) {
        Validate.isTrue(cls != null, "The class must not be null");
        List<Field> allFields = new ArrayList<>();

        for (Class<?> currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
        }

        return allFields;
    }
}
