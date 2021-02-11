package com.ggemo.va.goingmerry.handlerselector.impl;

import com.ggemo.va.business.pipeline.RichListPplBusiness;
import com.ggemo.va.goingmerry.handlerselector.HandlerSelector;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.handlerselector.handlerregistry.impl.ClassicHandlerRegistry;
import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.opentity.OpRichContext;
import com.ggemo.va.step.ClassicOpStep;
import com.ggemo.va.step.useutils.CacheStepUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>根据条件找实现类, {@link HandlerSelector}的classic实现
 *
 * <p>具体实现逻辑见{@link ClassicHandlerSelector#ClassicHandlerSelector()}
 */
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
        // 初始化缓存工具, 方便从缓存取结果
        // 一个condition如果没有缓存的话, 要经过下面那些步骤(解析condition, 匹配解析结果等)才能拿到实现类
        CacheStepUtil<ClassicHandlerSelector.Context<ClassicConditionAnalyseResult>, ClassicConditionAnalyzer.Req,
                OpHandler<?, ?>>
                cacheStepUtil =
                new CacheStepUtil<>(
                        c -> new ClassicConditionAnalyzer.Req(c.getMmCondition(), c.getMmHandlerClazz()),
                        Context::getResHandler,
                        (c, res) -> {
                            if (res == null) {
                                return;
                            }
                            c.setResHandler(res);
                            c.setEarlyReturn(true);
                        });

        // 从缓存中取值
        addStep(cacheStepUtil.getGetStep());

        // 对条件对象condition进行解析
        addStep(new ClassicOpStep<>(
                ClassicConditionAnalyzer.getInstance(),
                c -> new ClassicConditionAnalyzer.Req(c.getMmCondition(), c.getMmHandlerClazz()),
                Context::setAnalyseResult
        ));

        // 根据解析结果找到匹配的OpHandler
        addStep(new ClassicOpStep<>(
                ClassicHandlerRegistry.getInstance(),
                c -> new ClassicHandlerRegistry.Req(c.getMmHandlerClazz(), c.getAnalyseResult()),
                Context::setResHandler
        ));

        // 写入缓存
        addStep(cacheStepUtil.getPutStep());
    }

    @Override
    protected Context<ClassicConditionAnalyseResult> generateContext(Req req) {
        Context<ClassicConditionAnalyseResult> context = new Context<>();
        context.setMmHandlerClazz(req.getHandlerClazz());
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
        Class<? extends OpHandler<?, ?>> mmHandlerClazz;
        Object mmCondition;

        AnalyseResult analyseResult;

        OpHandler<?, ?> resHandler;

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
