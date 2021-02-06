package com.ggemo.va.goingmerry.op;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ggemo.va.goingmerry.conditionbeanspool.ConditionedBeansPool;
import com.ggemo.va.goingmerry.OpConditionWrapper;
import com.ggemo.va.goingmerry.annotation.OpService;
import com.ggemo.va.handler.OpHandler;
import com.ggemo.va.step.BaseOpStep;

public abstract class GmStep<Context, Condition, Req, Res>
        extends BaseOpStep<Context, Req, Res> implements ApplicationContextAware {
    ApplicationContext applicationContext;

    protected Class<OpHandler<Req, Res>> handlerClazz;

    protected abstract Condition generateCondition(Context context);

    protected abstract ConditionedBeansPool getConditionedBeansPool();

    protected OpHandler<Req, Res> selectHandler(Condition condition) {

        OpHandler<Req, Res> bean = (OpHandler<Req, Res>) getConditionedBeansPool().get(handlerClazz, condition);

        if (bean != null) {
            return bean;
        }

        if (getConditionedBeansPool().isConstructed(handlerClazz)) {
            throw new RuntimeException("// todo");
        }

        initConditionedBeans(handlerClazz);

        bean = (OpHandler<Req, Res>) getConditionedBeansPool().get(handlerClazz, condition);

        if (bean != null) {
            return bean;
        }

        throw new RuntimeException("123");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(Context context) {
        Condition condition = generateCondition(context);
        Req req = generateReq(context);
        OpHandler<Req, Res> handler = selectHandler(condition);
        Res res = handler.handle(req);
        applyRes(context, res);
    }

    private void initConditionedBeans(Class<OpHandler<Req, Res>> handlerClazz) {
        String[] beanNames = applicationContext.getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {
            OpService opService = applicationContext.findAnnotationOnBean(beanName, OpService.class);
            if (opService == null) {
                continue;
            }
            OpConditionWrapper<?> opConditionWrapper = BeanUtils.instantiateClass(opService.conditionClass());
            getConditionedBeansPool().put(handlerClazz, opConditionWrapper.getCondition(),
                    applicationContext.getBean(beanName, handlerClazz));
        }
        getConditionedBeansPool().setConstructed(handlerClazz);
    }
}
