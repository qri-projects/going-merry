package com.ggemo.va.goingmerry.handler;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ggemo.va.handler.OpHandler;

public class FilterHandler<T, C extends Collection<T>>
        implements OpHandler<Collection<T>, Collection<T>> {
    Predicate<T> predicate;
    Supplier<C> collectionFactory;

    public FilterHandler(Predicate<T> predicate, Supplier<C> collectionFactory) {
        this.predicate = predicate;
        this.collectionFactory = collectionFactory;
    }

    @Override
    public Collection<T> handle(Collection<T> values) {
        return values.stream().filter(predicate).collect(Collectors.toCollection(collectionFactory));
    }
}
