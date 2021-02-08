package com.ggemo.va.goingmerry.op;

import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import com.ggemo.va.contextadaptor.step.StepReqGenerator;
import com.ggemo.va.contextadaptor.step.StepResApplier;
import com.ggemo.va.goingmerry.GgConditionWrapper;
import com.ggemo.va.goingmerry.annotation.OpService;
import com.ggemo.va.goingmerry.conditionbeanspool.ConditionedBeansPool;
import com.ggemo.va.goingmerry.conditionbeanspool.HashMapConditionedBeansPool;
import com.ggemo.va.goingmerry.op.step.MmConditionGenerator;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

public class ClassicGmStep<Context, Condition, Req, Res> extends GmStep<Context, Condition, Req, Res> {
    private MmConditionGenerator<Collection<Condition>, Context> mmConditionGenerator;
    private StepReqGenerator<Req, Context> reqGenerator;
    private StepResApplier<Context, Res> resApplier;
    private static final ConditionedBeansPool CONDITIONED_BEANS_POOL = HashMapConditionedBeansPool.getInstance();

    public ClassicGmStep(Class<? extends OpHandler<Req, Res>> handlerClass,
                         MmConditionGenerator<Collection<Condition>, Context> mmConditionGenerator,
                         StepReqGenerator<Req, Context> reqGenerator,
                         StepResApplier<Context, Res> resApplier) {
        this.handlerClazz = handlerClass;
        this.mmConditionGenerator = mmConditionGenerator;
        this.reqGenerator = reqGenerator;
        this.resApplier = resApplier;
    }

    protected ConditionedBeansPool getConditionedBeansPool() {
        return CONDITIONED_BEANS_POOL;
    }

    @Override
    protected Collection<Condition> generateMmConditions(Context context) {
        return mmConditionGenerator.gen(context);
    }

    @Override
    protected OpHandler<Req, Res> selectHandler(Collection<Condition> mmConditions) {
        if (!CONDITIONED_BEANS_POOL.isConstructed(handlerClazz)) {
            initConditionedBeans(handlerClazz);
        }
        for (Condition mmCondition : mmConditions) {
            OpHandler<Req, Res> bean = (OpHandler<Req, Res>) CONDITIONED_BEANS_POOL.get(handlerClazz, mmCondition);

            if (bean != null) {
                return bean;
            }

            if (CONDITIONED_BEANS_POOL.isConstructed(handlerClazz)) {
                throw new RuntimeException("// todo");
            }

            initConditionedBeans(handlerClazz);

            bean = (OpHandler<Req, Res>) CONDITIONED_BEANS_POOL.get(handlerClazz, mmCondition);

            if (bean != null) {
                return bean;
            }


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

    private void initConditionedBeans(Class<? extends OpHandler<Req, Res>> handlerClazz) throws IllegalAccessException {
        String[] beanNames = getApplicationContext().getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {
            OpService opService = getApplicationContext().findAnnotationOnBean(beanName, OpService.class);
            if (opService == null) {
                continue;
            }
            for (Class<? extends GgConditionWrapper<?>> wrapperClazz : opService.value()) {
                GgConditionWrapper<?> ggConditionWrapper = BeanUtils.instantiateClass(wrapperClazz);
                Object ggCondition = ggConditionWrapper.getGgCondition();

                Class<?> clazz = ggCondition.getClass();
                String className = clazz.getCanonicalName();

                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {
                    String fieldKey = className + "#" + field.getName();
                    Object fieldValue = field.get(ggCondition);
                    CONDITIONED_BEANS_POOL.registerHandler(fieldKey, fieldValue,
                            getApplicationContext().getBean(beanName, handlerClazz));
                }
            }
        }

        CONDITIONED_BEANS_POOL.setConstructed(handlerClazz);
    }
}
