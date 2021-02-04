package com.ggemo.va.goingmerry.business.getopproxyfieldsbusiness;

import java.lang.reflect.Field;

import lombok.Data;

@Data
public class GetOpProxyFieldsContext extends FilterContext<Field> {
    Class<?> clazz;
}
