package com.ggemo.va.goingmerry.business.getopproxyfieldsbusiness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ReflectionUtils;

import com.ggemo.va.business.pipeline.ListPplBusiness;
import com.ggemo.va.goingmerry.annotation.OpProxy;
import com.ggemo.va.goingmerry.business.injectopproxyfields.InjectOpProxyFieldsBusiness;
import com.ggemo.va.goingmerry.handler.GetClassFieldsHandler;
import com.ggemo.va.goingmerry.handler.ListFilterHandler;
import com.ggemo.va.goingmerry.step.FilterStep;
import com.ggemo.va.step.ClassicOpStep;

public class GetOpProxyFieldsBusiness extends ListPplBusiness<GetOpProxyFieldsContext, Class<?>, Collection<Field>> {
    public GetOpProxyFieldsBusiness() {
        addStep(new ClassicOpStep<>(
                GetOpProxyFieldsContext::getClazz,
                (c, l) -> {
                    c.setBefore(l);
                    return c;
                },
                new GetClassFieldsHandler()));

        addStep(new FilterStep<>(field -> field.getAnnotation(OpProxy.class) != null, ArrayList::new));
    }

    @Override
    protected GetOpProxyFieldsContext generateContext(Class<?> clazz) {
        GetOpProxyFieldsContext context = new GetOpProxyFieldsContext();
        context.setClazz(clazz);
        return context;
    }

    @Override
    protected Collection<Field> castToRes(GetOpProxyFieldsContext context) {
        return context.getAfter();
    }
}
