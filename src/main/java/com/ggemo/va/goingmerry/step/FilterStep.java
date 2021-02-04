package com.ggemo.va.goingmerry.step;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.ggemo.va.goingmerry.business.getopproxyfieldsbusiness.FilterContext;
import com.ggemo.va.goingmerry.handler.FilterHandler;
import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.AbstractOpStep;
import com.ggemo.va.step.OpStep;

public class FilterStep<T, C extends Collection<T>>
        extends AbstractOpStep<FilterContext<T>, Collection<T>, Collection<T>>
        implements OpStep<FilterContext<T>, Collection<T>, Collection<T>> {
    public FilterStep(Predicate<T> predicate, Supplier<C> supplier) {
        super(new FilterHandler<>(predicate, supplier));
    }

    @Override
    protected Collection<T> generateReq(FilterContext<T> context) {
        return context.getBefore();
    }

    @Override
    protected FilterContext<T> applyRes(FilterContext<T> context, Collection<T> res) {
        context.setAfter(res);
        return context;
    }
}
