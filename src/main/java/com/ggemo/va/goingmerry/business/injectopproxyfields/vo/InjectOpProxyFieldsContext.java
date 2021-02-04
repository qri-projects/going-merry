package com.ggemo.va.goingmerry.business.injectopproxyfields.vo;

import java.lang.reflect.Field;
import java.util.List;

import lombok.Data;

@Data
public class InjectOpProxyFieldsContext {
    Object bean;
    List<Field> beanFields;
}
