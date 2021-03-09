package com.ggemo.va.goingmerry.gmserviceselector.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ggemo.va.business.pipeline.RichListPplBusiness;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.gmserviceselector.GmServiceSelector;
import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.ConditionAnalyseResult;
import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.gmserviceselector.gmserviceregistry.impl.ClassicGmServiceRegistry;
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

    @PostConstruct
    public void init() {
        super.init();
    }

    @Autowired
    ClassicConditionAnalyzer conditionAnalyzer;

    @Autowired
    ClassicGmServiceRegistry gmServiceRegistry;

    @Override
    public void initSteps() {
        // 初始化缓存工具, 方便从缓存取结果
        // 一个condition如果没有缓存的话, 要经过下面那些步骤(解析condition, 匹配解析结果等)才能拿到实现类
        CacheStepUtil<ClassicGmServiceSelector.Context<ClassicConditionAnalyseResult>, Object,
                GmService<?>> cacheStepUtil = new CacheStepUtil<>(
                Context::getMmCondition,
                Context::getResGmService,
                (c, res) -> {
                    if (res == null) {
                        return;
                    }
                    c.setResGmService(res);
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

        // 根据解析结果找到匹配的GmService
        addStep(new ClassicOpStep<>(
                gmServiceRegistry,
                c -> new ClassicGmServiceRegistry.Req(c.getMmGmServiceClazz(), c.getAnalyseResult()),
                Context::setResGmService
        ));

        // 写入缓存
        addStep(cacheStepUtil.getPutStep());
    }

    @Override
    protected Context<ClassicConditionAnalyseResult> generateContext(Req req) {
        Context<ClassicConditionAnalyseResult> context = new Context<>();
        context.setMmGmServiceClazz(req.getGmServiceClazz());
        context.setMmCondition(req.getMmCondition());
        return context;
    }

    @Override
    protected GmService<?> castToRes(Context context) {
        return context.getResGmService();
    }

    @Override
    public <Condition, S extends GmService<Condition>> S select(
            Class<S> gmServiceClazz, Condition mmCondition) {
        Req req = new Req();
        req.setMmCondition(mmCondition);
        req.setGmServiceClazz(gmServiceClazz);
        return (S) handle(req);
    }

    @Data
    @NoArgsConstructor
    public static class Context<AnalyseResult extends ConditionAnalyseResult> implements OpRichContext {
        Class<? extends GmService<?>> mmGmServiceClazz;
        Object mmCondition;

        AnalyseResult analyseResult;

        GmService<?> resGmService;

        boolean earlyReturn = false;

        @Override
        public boolean getEarlyReturn() {
            return earlyReturn;
        }
    }

    @Data
    @NoArgsConstructor
    static class Req {
        Class<? extends GmService<?>> gmServiceClazz;
        Object mmCondition;
    }
}
