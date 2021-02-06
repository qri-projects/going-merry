package com.ggemo.va.goingmerry.op;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.conditionbeanspool.ConditionedBeansPool;
import com.ggemo.va.goingmerry.conditionbeanspool.HashMapConditionedBeansPool;
import com.ggemo.va.goingmerry.op.step.StepConditionGenerator;
import com.ggemo.va.handler.OpHandler;

public class ClassicGmStep<Context, Condition, Req, Res> extends GmStep<Context, Condition, Req, Res> {
    private StepConditionGenerator<Condition, Context> conditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;
    private static final ConditionedBeansPool CONDITIONED_BEANS_POOL = HashMapConditionedBeansPool.getInstance();

    public ClassicGmStep(Class<OpHandler<Req, Res>> handlerClass,
                         StepConditionGenerator<Condition, Context> conditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.conditionGenerator = conditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
    }

    @Override
    protected ConditionedBeansPool getConditionedBeansPool() {
        return CONDITIONED_BEANS_POOL;
    }

    @Override
    protected Condition generateCondition(Context context) {
        return conditionGenerator.generate(context);
    }

    @Override
    protected Req generateReq(Context context) {
        return reqGenerator.generate(context);
    }

    @Override
    protected Context applyRes(Context context, Res res) {
        return resApplier.apply(context, res);
    }
}
