package com.ggemo.va.goingmerry.handler.handlerregistry.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import com.ggemo.va.goingmerry.GgConditionWrapper;
import com.ggemo.va.goingmerry.annotation.OpService;
import com.ggemo.va.goingmerry.handler.handlerSelector.impl.ClassicHandlerSelector;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handler.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handler.handlerregistry.HandlerRegistry;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

public class ClassicHandlerRegistry implements HandlerRegistry<ClassicConditionAnalyseResult>, OpHandler<ClassicHandlerSelector.Context<ClassicConditionAnalyseResult>, OpHandler<?, ?>> {
    private static final Map<Class<? extends OpHandler>,
            Map<OpHandler<?, ?>, ClassicConditionAnalyseResult>> GG_HANDLERS_HOLDER = new HashMap<>();
    private static final Set<Class<? extends OpHandler<?, ?>>> REGISTERED_SET = new HashSet<>();

    private static ClassicHandlerRegistry INSTANCE = null;

    public static ClassicHandlerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicHandlerRegistry(ClassicConditionAnalyzer.getInstance());
        }
        return INSTANCE;
    }

    private final ConditionAnalyzer<ClassicConditionAnalyseResult> analyzer;

    private ClassicHandlerRegistry(ConditionAnalyzer<ClassicConditionAnalyseResult> analyzer) {
        this.analyzer = analyzer;
    }

    private ApplicationContext getApplicationContext() {
        return GoingMerryConfig.getApplicationContext();
    }

    @Override
    public void register(ClassicConditionAnalyseResult analyseResult, OpHandler<?, ?> handler) {

        for (Class handlerClazz : getHandlerSuperClasses(handler.getClass())) {
            if (!GG_HANDLERS_HOLDER.containsKey(handlerClazz)) {
                GG_HANDLERS_HOLDER.put(handlerClazz, new HashMap<>());
            }
            GG_HANDLERS_HOLDER.get(handlerClazz).put(handler, analyseResult);
        }
    }

    @Override
    public OpHandler<?, ?> findHandler(ClassicConditionAnalyseResult mmAnalyseResult,
                                       Class<? extends OpHandler<?, ?>> handlerClazz) {
        // key: handler, value: 命中的special条件数
        Map<OpHandler<?, ?>, Integer> matchedHandlers = new HashMap<>();

        if (!GG_HANDLERS_HOLDER.containsKey(handlerClazz)) {
            // todo: no handler matches
            return null;
        }

        // 遍历handler和它的ggAnalyseResult
        GG_HANDLERS_HOLDER.get(handlerClazz).forEach((handler, ggAnalyseResult) ->

                // 遍历mmAnalyseResult各个条件, 判断ggAnalyseResult是否匹配
                mmAnalyseResult.forEach((field, mmValue) -> {

                    Object ggValue = ggAnalyseResult.get(field);

                    // mmValue不为null, 完美匹配ggValue
                    if (mmValue != null && mmValue.equals(ggValue)) {
                        if (!matchedHandlers.containsKey(handler)) {
                            // 将其权重+1
                            matchedHandlers.put(handler, 1);
                        } else {
                            Integer handlerWeight = matchedHandlers.get(handler);

                            // 权重小于0说明该handler已经不匹配了, 不用+
                            if (handlerWeight >= 0) {
                                // 将其权重+1
                                matchedHandlers.put(handler, handlerWeight + 1);
                            }

                        }
                    } else if (mmValue != null && ggValue == null) {
                        // ggValue为null, 表示都能匹配, 其权重低于完美匹配的, 权重为0
                        if (!matchedHandlers.containsKey(handler)) {
                            matchedHandlers.put(handler, 0);
                        }
                    } else if (ggValue != null) {
                        // ggValue不为null, 并且没有完美匹配到, 判断为其不匹配, 权重设为-1
                        matchedHandlers.put(handler, -1);
                    } else if (mmValue == null) {
                        // pass, mmValue为null表示不根据此条字段进行分发, 所有皆可匹配.
                    }
                })
        );

        // 在所有匹配中选出权重最高的
        OpHandler<?, ?> mostMatchedHandler = null;
        int mostMatchedWeight = -1;

        for (OpHandler<?, ?> matchedHandler : matchedHandlers.keySet()) {
            Integer matchedWeight = matchedHandlers.get(matchedHandler);
            if (matchedWeight > mostMatchedWeight) {
                mostMatchedWeight = matchedWeight;
                mostMatchedHandler = matchedHandler;
            }
        }
        if (mostMatchedHandler == null) {
            throw new RuntimeException("// todo: 没有匹配的handler");
        }
        return mostMatchedHandler;
    }

    @Override
    public void initRegister(Class<? extends OpHandler<?, ?>> handlerClazz) {
        this.innerInitRegister(getApplicationContext(), handlerClazz);
        REGISTERED_SET.add(handlerClazz);
    }

    @Override
    public boolean registered(Class<? extends OpHandler<?, ?>> handlerClazz) {
        return REGISTERED_SET.contains(handlerClazz);
    }

    private void innerInitRegister(ApplicationContext applicationContext,
                                   Class<? extends OpHandler<?, ?>> handlerClazz) {

        String[] beanNames = applicationContext.getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {
            OpService opService = applicationContext.findAnnotationOnBean(beanName, OpService.class);
            if (opService == null) {
                continue;
            }
            for (Class<? extends GgConditionWrapper<?>> wrapperClazz : opService.value()) {
                GgConditionWrapper<?> ggConditionWrapper = BeanUtils.instantiateClass(wrapperClazz);
                Object ggCondition = ggConditionWrapper.getGgCondition();

                ClassicConditionAnalyseResult result = analyzer.analyse(ggCondition, handlerClazz);

                register(result, applicationContext.getBean(beanName, handlerClazz));
            }
        }
    }

    private static Set<Class> getInterfacesAndSuperClass(Class clazz) {
        Set<Class> set = new HashSet<>();
        set.addAll(Arrays.asList(clazz.getInterfaces()));
        set.add(clazz.getSuperclass());
        return set;
    }

    private static Set<Class> getHandlerSuperClasses(Class clazz) {
        if (clazz == null) {
            return new HashSet<>();
        }
        if (clazz.equals(OpHandler.class)) {
            return new HashSet<Class>(){{add(OpHandler.class);}};
        }
        Set<Class> res = new HashSet<>();
        for (Class interfaze : getInterfacesAndSuperClass(clazz)) {
            Set<Class> interfazeRes = getHandlerSuperClasses(interfaze);
            if (interfazeRes.size() == 0) {
                continue;
            }
            res.addAll(getHandlerSuperClasses(interfaze));
        }
        if (res.size() != 0) {
            res.add(clazz);
        }
        return res;
    }

    @Override
    public OpHandler<?, ?> handle(
            ClassicHandlerSelector.Context<ClassicConditionAnalyseResult> req) {
        if (!this.registered(req.getHandlerClazz())) {
            this.initRegister(req.getHandlerClazz());
        }
        return this.findHandler(req.getAnalyseResult(), req.getHandlerClazz());
    }
}
