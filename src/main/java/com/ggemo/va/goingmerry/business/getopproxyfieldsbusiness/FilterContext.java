package com.ggemo.va.goingmerry.business.getopproxyfieldsbusiness;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class FilterContext<T> {
    Collection<T> before;
    Collection<T> after;
}
