package com.ggemo.va.goingmerry.op;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.gmservice.GmHandlerService;
import com.ggemo.va.goingmerry.gmserviceselector.GmServiceSelector;
import com.ggemo.va.goingmerry.op.step.MmConditionGenerator;
import com.ggemo.va.goingmerry.utiils.ApplicationContextUtil;

/**
 * <p>GmStep相关设计见docs/gm-design.md
 */
public class ClassicGmStep<Context, Condition, Req, Res> extends
        GmserviceSelectorBasedGmStep<Context, Condition, Req, Res> {
    private MmConditionGenerator<Condition, Context> mmConditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;
    private GmServiceSelector gmServiceSelector;

    public ClassicGmStep(Class<? extends GmHandlerService<Condition, Req, Res>> handlerClass,
                         MmConditionGenerator<Condition, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.mmConditionGenerator = mmConditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
        this.gmServiceSelector = null;
    }

    public ClassicGmStep(Class<? extends GmHandlerService<Condition, Req, Res>> handlerClass,
                         MmConditionGenerator<Condition, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier,
                         GmServiceSelector gmServiceSelector) {
        this(handlerClass, mmConditionGenerator, reqGenerator, resApplier);
        this.gmServiceSelector = gmServiceSelector;
    }

    @Override
    protected Condition generateMmCondition(Context context) {
        return mmConditionGenerator.gen(context);
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
    public GmServiceSelector getGmServiceSelector() {
        if (gmServiceSelector != null) {
            return gmServiceSelector;
        }
        gmServiceSelector = ApplicationContextUtil.getApplicationContext().getBean(GmServiceSelector.class);
        return gmServiceSelector;
    }
}
