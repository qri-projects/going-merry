package com.ggemo.va.goingmerry.op;

import org.springframework.context.ApplicationContext;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.handlerselector.impl.ClassicHandlerSelector;
import com.ggemo.va.goingmerry.handlerselector.HandlerSelector;
import com.ggemo.va.goingmerry.op.step.MmConditionGenerator;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

public class ClassicGmStep<Context, Condition, Req, Res> extends HandlerSelectorBasedGmStep<Context, Condition, Req, Res> {
    private MmConditionGenerator<Condition, Context> mmConditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;
    private HandlerSelector handlerSelector;

    public ClassicGmStep(Class<? extends OpHandler<Req, Res>> handlerClass,
                         MmConditionGenerator<Condition, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier,
                         HandlerSelector handlerSelector) {
        this.handlerClazz = handlerClass;
        this.mmConditionGenerator = mmConditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
        this.handlerSelector = handlerSelector;
    }

    public ClassicGmStep(Class<? extends OpHandler<Req, Res>> handlerClass,
                         MmConditionGenerator<Condition, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.mmConditionGenerator = mmConditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
        this.handlerSelector = ClassicHandlerSelector.getInstance();
    }

    @Override
    protected Condition generateMmCondition(Context context) {
        return mmConditionGenerator.gen(context);
    }

    @Override
    protected ApplicationContext getApplicationContext() {
        return GoingMerryConfig.getApplicationContext();
    }

    @Override
    protected Req generateReq(Context context) {
        return reqGenerator.generate(context);
    }

    @Override
    protected void applyRes(Context context, Res res) {
        resApplier.apply(context, res);
    }

    @Override
    public HandlerSelector getHandlerSelector() {
        return handlerSelector;
    }
}
