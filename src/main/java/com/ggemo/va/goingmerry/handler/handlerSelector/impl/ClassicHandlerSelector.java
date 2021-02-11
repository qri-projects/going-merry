package com.ggemo.va.goingmerry.handler.handlerSelector.impl;

import com.ggemo.va.business.pipeline.RichListPplBusiness;
import com.ggemo.va.goingmerry.handler.handlerSelector.HandlerSelector;
import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handlerregistry.impl.ClassicHandlerRegistry;
import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.opentity.OpRichContext;
import com.ggemo.va.step.ClassicOpStep;
import com.ggemo.va.step.useutils.CacheStepUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

public class ClassicHandlerSelector
        extends RichListPplBusiness<ClassicHandlerSelector.Context<ClassicConditionAnalyseResult>,
        ClassicHandlerSelector.Req, OpHandler<?, ?>>
        implements HandlerSelector {
    private static ClassicHandlerSelector INSTANCE;

    public static ClassicHandlerSelector getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ClassicHandlerSelector();
        return INSTANCE;
    }

    private ClassicHandlerSelector() {
        // init cacheUtil
        CacheStepUtil<ClassicHandlerSelector.Context<ClassicConditionAnalyseResult>, Object, OpHandler<?, ?>>
                cacheStepUtil =
                new CacheStepUtil<>(Context::getMmCondition, Context::getResHandler, (c, res) -> {
                    if (res == null) {
                        return;
                    }
                    c.setResHandler(res);
                    c.setEarlyReturn(true);
                });

        // find in cache
        addStep(cacheStepUtil.getGetStep());

        // analyse condition
        addStep(new ClassicOpStep<>(ClassicConditionAnalyzer.getInstance(),
                c -> new ClassicConditionAnalyzer.Req(c.getMmCondition(), c.getHandlerClazz()),
                Context::setAnalyseResult
        ));

        // find handler in registry
        addStep(new ClassicOpStep<>(ClassicHandlerRegistry.getInstance(), c -> c,
                Context::setResHandler));

        // fill into cache
        addStep(cacheStepUtil.getPutStep());
    }

    @Override
    protected Context<ClassicConditionAnalyseResult> generateContext(Req req) {
        Context<ClassicConditionAnalyseResult> context = new Context<>();
        context.setHandlerClazz(req.getHandlerClazz());
        context.setMmCondition(req.getMmCondition());
        return context;
    }

    @Override
    protected OpHandler<?, ?> castToRes(Context context) {
        return context.getResHandler();
    }

    @Override
    public OpHandler<?, ?> select(Class<? extends OpHandler<?, ?>> handlerClazz, Object mmCondition) {
        Req req = new Req();
        req.setMmCondition(mmCondition);
        req.setHandlerClazz(handlerClazz);
        return handle(req);
    }

    @Data
    @NoArgsConstructor
    public static class Context<AnalyseResult extends ConditionAnalyseResult> implements OpRichContext {
        Class<? extends OpHandler<?, ?>> handlerClazz;
        OpHandler<?, ?> resHandler;

        Object mmCondition;
        AnalyseResult analyseResult;

        boolean earlyReturn = false;

        @Override
        public boolean getEarlyReturn() {
            return earlyReturn;
        }
    }

    @Data
    @NoArgsConstructor
    static class Req {
        Class<? extends OpHandler<?, ?>> handlerClazz;
        Object mmCondition;
    }
}
