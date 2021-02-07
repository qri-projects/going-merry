package com.ggemo.va.goingmerry.op;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.OpConditionWrapper;
import com.ggemo.va.goingmerry.annotation.OpService;
import com.ggemo.va.goingmerry.conditionbeanspool.ConditionedBeansPool;
import com.ggemo.va.goingmerry.conditionbeanspool.HashMapConditionedBeansPool;
import com.ggemo.va.goingmerry.op.step.StepConditionGenerator;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

public class ClassicGmStep<Context, Condition, Req, Res> extends GmStep<Context, Condition, Req, Res> {
    private StepConditionGenerator<Collection<Condition>, Context> conditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;
    private static final ConditionedBeansPool CONDITIONED_BEANS_POOL = HashMapConditionedBeansPool.getInstance();

    public ClassicGmStep(Class<? extends OpHandler<Req, Res>> handlerClass,
                         StepConditionGenerator<Collection<Condition>, Context> conditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.conditionGenerator = conditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
    }

    protected ConditionedBeansPool getConditionedBeansPool() {
        return CONDITIONED_BEANS_POOL;
    }

    @Override
    protected Collection<Condition> generateConditions(Context context) {
        return conditionGenerator.generate(context);
    }

    @Override
    protected OpHandler<Req, Res> selectHandler(Collection<Condition> conditions) {

        OpHandler<Req, Res> bean = (OpHandler<Req, Res>) CONDITIONED_BEANS_POOL.get(handlerClazz, conditions);

        if (bean != null) {
            return bean;
        }

        if (CONDITIONED_BEANS_POOL.isConstructed(handlerClazz)) {
            throw new RuntimeException("// todo");
        }

        initConditionedBeans(handlerClazz);

        bean = (OpHandler<Req, Res>) CONDITIONED_BEANS_POOL.get(handlerClazz, conditions);

        if (bean != null) {
            return bean;
        }

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

    private void initConditionedBeans(Class<? extends OpHandler<Req, Res>> handlerClazz) {
        String[] beanNames = getApplicationContext().getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {
            OpService opService = getApplicationContext().findAnnotationOnBean(beanName, OpService.class);
            if (opService == null) {
                continue;
            }
            for (Class<? extends OpConditionWrapper<?>> conditionClass : opService.value()) {
                OpConditionWrapper<?> opConditionWrapper = BeanUtils.instantiateClass(conditionClass);
                CONDITIONED_BEANS_POOL.put(handlerClazz, opConditionWrapper.getCondition(),
                        getApplicationContext().getBean(beanName, handlerClazz));
            }
        }
        CONDITIONED_BEANS_POOL.setConstructed(handlerClazz);
    }
}
