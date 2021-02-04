package com.ggemo.va.goingmerry.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListFilterHandler<T> extends FilterHandler<T, List<T>>{
    public ListFilterHandler(Predicate<T> predicate) {
        super(predicate, ArrayList::new);
    }
}
