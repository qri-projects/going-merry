package com.ggemo.va.goingmerry.op;

import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.op.step.MmConditionGenerator;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

public class ClassicGmStep<Context, Condition, Req, Res> extends GmStep<Context, Condition, Req, Res> {
    private MmConditionGenerator<Collection<Condition>, Context> mmConditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;

    public ClassicGmStep(Class<? extends OpHandler<Req, Res>> handlerClass,
                         MmConditionGenerator<Collection<Condition>, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.mmConditionGenerator = mmConditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
    }


    @Override
    protected Collection<Condition> generateMmConditions(Context context) {
        return mmConditionGenerator.gen(context);
    }

    @Override
    protected OpHandler<Req, Res> selectHandler(Collection<Condition> mmConditions) {
        throw new RuntimeException("123");
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

    private void initConditionedBeans(Class<? extends OpHandler<Req, Res>> handlerClazz) throws IllegalAccessException {

    }
}
