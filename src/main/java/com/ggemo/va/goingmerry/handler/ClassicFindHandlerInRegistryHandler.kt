package com.ggemo.va.goingmerry.handler

import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult
import com.ggemo.va.goingmerry.handler.handlerregistry.ClassicHandlerRegistry
import com.ggemo.va.handler.OpHandler

class ClassicFindHandlerInRegistryHandler
    : OpHandler<SelectHandlerBusiness.Context<ClassicConditionAnalyseResult>, OpHandler<*, *>> {
    private val registry: ClassicHandlerRegistry = ClassicHandlerRegistry.getInstance();

    companion object {
        private val INSTANCE = ClassicFindHandlerInRegistryHandler();

        fun getInstance(): ClassicFindHandlerInRegistryHandler {
            return INSTANCE
        }
    }

    override fun handle(req: SelectHandlerBusiness.Context<ClassicConditionAnalyseResult>): OpHandler<*, *> {
        if (!registry.registered(req.getHandlerClazz())) {
            registry.initRegister(req.getHandlerClazz())
        }
        return registry.findHandler(req.getAnalyseResult(), req.getHandlerClazz())
    }
}