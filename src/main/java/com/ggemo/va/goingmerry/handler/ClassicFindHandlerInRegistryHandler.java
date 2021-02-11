package com.ggemo.va.goingmerry.handler;

import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handlerregistry.ClassicHandlerRegistry;
import com.ggemo.va.handler.OpHandler;

public class ClassicFindHandlerInRegistryHandler
        implements OpHandler<SelectHandlerBusiness.Context<ClassicConditionAnalyseResult>, OpHandler<?, ?>> {
    private final ClassicHandlerRegistry registry;

    public ClassicFindHandlerInRegistryHandler() {
        registry  = ClassicHandlerRegistry.getInstance();
    }

    private static ClassicFindHandlerInRegistryHandler INSTANCE;

    public static ClassicFindHandlerInRegistryHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicFindHandlerInRegistryHandler();
        }
        return INSTANCE;
    }

    @Override
    public OpHandler<?, ?> handle(SelectHandlerBusiness.Context<ClassicConditionAnalyseResult> req) {
        if (!registry.registered(req.getHandlerClazz())) {
            registry.initRegister(req.getHandlerClazz());
        }
        return registry.findHandler(req.getAnalyseResult(), req.getHandlerClazz());
    }
}
