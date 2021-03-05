package com.ggemo.va.goingmerry.handlerselector.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ggemo.va.business.pipeline.RichListPplBusiness;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.GmServiceSelector;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.handlerselector.handlerregistry.impl.ClassicGmServiceRegistry;
import com.ggemo.va.opentity.OpRichContext;
import com.ggemo.va.step.ClassicOpStep;
import com.ggemo.va.step.useutils.CacheStepUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>根据条件找实现类, {@link GmServiceSelector}的classic实现<br/>
 * 使用责任链的方式理清逻辑
 */
@Component
public class ClassicGmServiceSelector
        extends RichListPplBusiness<ClassicGmServiceSelector.Context<ClassicConditionAnalyseResult>,
        ClassicGmServiceSelector.Req, GmService<?>>
        implements GmServiceSelector {

    @Autowired
    ClassicConditionAnalyzer conditionAnalyzer;

    @Autowired
    ClassicGmServiceRegistry handlerRegistry;

    @PostConstruct
    public void init() {
        // 初始化缓存工具, 方便从缓存取结果
        // 一个condition如果没有缓存的话, 要经过下面那些步骤(解析condition, 匹配解析结果等)才能拿到实现类
        CacheStepUtil<ClassicGmServiceSelector.Context<ClassicConditionAnalyseResult>, Object,
                GmService<?>> cacheStepUtil = new CacheStepUtil<>(
                Context::getMmCondition,
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
                conditionAnalyzer,
                Context::getMmCondition,
                Context::setAnalyseResult
        ));

        // 根据解析结果找到匹配的OpHandler
        addStep(new ClassicOpStep<>(
                handlerRegistry,
                c -> new ClassicGmServiceRegistry.Req(c.getMmHandlerClazz(), c.getAnalyseResult()),
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
    protected GmService<?> castToRes(Context context) {
        return context.getResHandler();
    }

    @Override
    public <Condition, S extends GmService<Condition>> S select(
            Class<S> handlerClazz, Condition mmCondition) {
        Req req = new Req();
        req.setMmCondition(mmCondition);
        req.setHandlerClazz(handlerClazz);
        return (S) handle(req);
    }

    @Data
    @NoArgsConstructor
    public static class Context<AnalyseResult extends ConditionAnalyseResult> implements OpRichContext {
        Class<? extends GmService<?>> mmHandlerClazz;
        Object mmCondition;

        AnalyseResult analyseResult;

        GmService<?> resHandler;

        boolean earlyReturn = false;

        @Override
        public boolean getEarlyReturn() {
            return earlyReturn;
        }
    }

    @Data
    @NoArgsConstructor
    static class Req {
        Class<? extends GmService<?>> handlerClazz;
        Object mmCondition;
    }
}
