package com.ggemo.va.goingmerry.handlerselector.handlerregistry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import com.ggemo.va.goingmerry.gmservice.GgConditionWrapper;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.ConditionAnalyzer;
import com.ggemo.va.goingmerry.handlerselector.handleranalyse.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.handlerselector.handlerregistry.HandlerRegistry;
import com.ggemo.va.goingmerry.utiils.GoingMerryConfig;
import com.ggemo.va.handler.OpHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>{@link HandlerRegistry}的classic实现
 */
public class ClassicHandlerRegistry implements HandlerRegistry<ClassicConditionAnalyseResult>,
        OpHandler<ClassicHandlerRegistry.Req, OpHandler<?, ?>> {
    // 存放handler的map, 取handler的时候按handlerClazz取, 根据analyseResult找权重最高的handler取
    private static final Map<Class<? extends OpHandler>, Map<OpHandler<?, ?>, List<ClassicConditionAnalyseResult>>>
            GG_HANDLERS_HOLDER = new HashMap<>();

    // 记录handler的class是否已经注册过了
    private static final Set<Class<? extends OpHandler<?, ?>>> REGISTERED_SET = new HashSet<>();

    // 单例
    private static ClassicHandlerRegistry INSTANCE = null;

    public static ClassicHandlerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassicHandlerRegistry();
        }
        return INSTANCE;
    }

    private final ConditionAnalyzer<ClassicConditionAnalyseResult> analyzer;

    private ClassicHandlerRegistry() {
        this.analyzer = ClassicConditionAnalyzer.getInstance();
    }

    private ApplicationContext getApplicationContext() {
        return GoingMerryConfig.getApplicationContext();
    }

    @Override
    public OpHandler<?, ?> findHandler(ClassicConditionAnalyseResult mmAnalyseResult,
                                       Class<? extends OpHandler<?, ?>> handlerClazz) {
        // handlerClazz还没有注册的话 先初始化注册
        if (!this.registered(handlerClazz)) {
            this.initRegister(handlerClazz);
        }

        // 存放匹配的handler. key: handler, value: 命中的special条件数
        Map<OpHandler<?, ?>, Map<ClassicConditionAnalyseResult, AtomicInteger>> matchedHandlers = new HashMap<>();

        if (!GG_HANDLERS_HOLDER.containsKey(handlerClazz)) {
            // todo: no handler matches
            return null;
        }

        // 遍历handler和它的ggAnalyseResults
        GG_HANDLERS_HOLDER.get(handlerClazz).forEach((handler, ggAnalyseResults) -> {
            Map<ClassicConditionAnalyseResult, AtomicInteger> analyseResult2WeightMap = new HashMap<>();
            matchedHandlers.put(handler, analyseResult2WeightMap);

            // 遍历ggAnalyseResults, 对每个ggAnalyseResult和mmAnalyseResult相比较
            ggAnalyseResultsLoop:
            for (ClassicConditionAnalyseResult ggAnalyseResult : ggAnalyseResults) {
                AtomicInteger weight = new AtomicInteger();
                analyseResult2WeightMap.put(ggAnalyseResult, weight);

                // 遍历mmAnalyseResult各个条件, 判断ggAnalyseResult是否匹配
                for (Object field : mmAnalyseResult.keySet()) {
                    Object mmValue = mmAnalyseResult.get(field);
                    Object ggValue = ggAnalyseResult.get(field);

                    if (mmValue != null && mmValue.equals(ggValue)) {
                        // mmValue不为null, 完美匹配ggValue

                        if (!(weight.get() < 0)) {
                            // 如果先前没有判定为不匹配, 则weight += 1
                            weight.addAndGet(1);
                        }
                    } else if (mmValue != null && ggValue == null) {
                        // ggValue为null, 表示都能匹配, 其权重低于完美匹配的, 权重为0, weight不加不减
                    } else if (ggValue != null) {
                        // ggValue不为null, 并且没有完美匹配到, 判断为其不匹配, 权重设为-1
                        weight.set(-1);
                        continue ggAnalyseResultsLoop;
                    } else if (mmValue == null) {
                        // pass, mmValue为null表示不根据此条字段进行分发, 所有皆可匹配.
                    }
                }
            }
        });

        // 在所有匹配中选出权重最高的. 用一个list来记录权重最高的handler, 来发现有两个handler权重一样且都为最高的情况
        int mostMatchedWeight = -1;
        List<OpHandler<?, ?>> mostMatchedHandlers = new ArrayList<>();

        // 遍历handler
        for (OpHandler<?, ?> matchedHandler : matchedHandlers.keySet()) {

            // 该handler下的最高权重
            OptionalInt matchedWeightOptional = matchedHandlers.get(matchedHandler)
                    .values()
                    .stream()
                    .mapToInt(AtomicInteger::get)
                    .max();
            if (!matchedWeightOptional.isPresent()) {
                continue;
            }
            int handlerMatchedWeight = matchedWeightOptional.getAsInt();

            // 与当前最高权重进行对比
            if (handlerMatchedWeight > mostMatchedWeight) {
                mostMatchedHandlers = new ArrayList<>();
                mostMatchedHandlers.add(matchedHandler);

                mostMatchedWeight = handlerMatchedWeight;
            } else if (handlerMatchedWeight == mostMatchedWeight) {
                mostMatchedHandlers.add(matchedHandler);
            }
        }

        if (CollectionUtils.isEmpty(mostMatchedHandlers)) {
            throw new RuntimeException("// todo: 没有匹配的handler");
        }

        if (mostMatchedHandlers.size() > 1) {
            throw new RuntimeException("// todo: 匹配冲突, 有两个handler匹配到");
        }
        return mostMatchedHandlers.get(0);
    }

    @Override
    public void initRegister(Class<? extends OpHandler<?, ?>> handlerClazz) {
        ApplicationContext applicationContext = getApplicationContext();

        // 遍历所有该类的bean的名字
        String[] beanNames = applicationContext.getBeanNamesForType(handlerClazz);
        for (String beanName : beanNames) {

            // 找到注解
            GmService gmService = applicationContext.findAnnotationOnBean(beanName, GmService.class);
            if (gmService == null) {
                continue;
            }

            // 找到注解下的所有ggCondition
            for (Class<? extends GgConditionWrapper<?>> conditionWrapperClazz : gmService.value()) {
                GgConditionWrapper<?> ggConditionWrapper = BeanUtils.instantiateClass(conditionWrapperClazz);
                Object ggCondition = ggConditionWrapper.getGgCondition();

                // 解析ggCondition
                ClassicConditionAnalyseResult result = analyzer.analyse(ggCondition);

                // 注册
                register(result, applicationContext.getBean(beanName, handlerClazz));
            }
        }

        // 标记该handlerClazz已经注册过了
        REGISTERED_SET.add(handlerClazz);
    }

    @Override
    public void register(ClassicConditionAnalyseResult analyseResult, OpHandler<?, ?> handler) {
        // 注册一个handler要将其所有父类注册上去
        Set<Class<?>> registerClasses = getHandlerSuperClasses(handler.getClass());
        for (Class handlerClazz : registerClasses) {

            // 下面的逻辑都是简单地将analyseResult, handler放进map中, 由于需要判空所以写起来有点复杂
            Map<OpHandler<?, ?>, List<ClassicConditionAnalyseResult>> handlerAnalyseResultMap;
            if (!GG_HANDLERS_HOLDER.containsKey(handlerClazz)) {
                handlerAnalyseResultMap = new HashMap<>();
                GG_HANDLERS_HOLDER.put(handlerClazz, handlerAnalyseResultMap);
            } else {
                handlerAnalyseResultMap = GG_HANDLERS_HOLDER.get(handlerClazz);
            }

            List<ClassicConditionAnalyseResult> analyseResults;
            if (!handlerAnalyseResultMap.containsKey(handler)) {
                analyseResults = new ArrayList<>();
                handlerAnalyseResultMap.put(handler, analyseResults);
            } else {
                analyseResults = handlerAnalyseResultMap.get(handler);
            }
            analyseResults.add(analyseResult);
        }
    }

    @Override
    public boolean registered(Class<? extends OpHandler<?, ?>> handlerClazz) {
        return REGISTERED_SET.contains(handlerClazz);
    }

    /**
     * <p>作为handler提供的方法
     */
    @Override
    public OpHandler<?, ?> handle(Req req) {
        return this.findHandler(req.getAnalyseResult(), req.getMmHandlerClazz());
    }

    @Data
    @AllArgsConstructor
    public static class Req {
        Class<? extends OpHandler<?, ?>> mmHandlerClazz;
        ClassicConditionAnalyseResult analyseResult;
    }

    /**
     * <p>工具方法, 找到给定类的所有继承自{@link OpHandler}的接口和父类
     * @param clazz 给定类
     * @return 所有父类和所有接口
     */
    private static Set<Class<?>> getHandlerSuperClasses(Class<?> clazz) {
        Set<Class<?>> res = new HashSet<>();

        // 递归的出口
        if (clazz == null) {
            return res;
        }
        if (clazz.equals(OpHandler.class)) {
            res.add(OpHandler.class);
            return res;
        }

        // 遍历所有上面一层的类和接口
        for (Class<?> superClazzes : getInterfacesAndSuperClass(clazz)) {
            // 递归地获取其所有继承自OpHandler的接口和父类
            Set<Class<?>> superRes = getHandlerSuperClasses(superClazzes);
            if (CollectionUtils.isEmpty(superRes)) {
                continue;
            }
            res.addAll(superRes);
        }

        // 不为空的话返回值加上自身
        if (!CollectionUtils.isEmpty(res)) {
            res.add(clazz);
        }
        return res;
    }

    /**
     * <p>工具方法, 找到给定类的接口和父类(一层)
     * @see #getHandlerSuperClasses
     */
    private static Set<Class<?>> getInterfacesAndSuperClass(Class<?> clazz) {
        Set<Class<?>> set = new HashSet<>();
        set.addAll(Arrays.asList(clazz.getInterfaces()));
        set.add(clazz.getSuperclass());
        return set;
    }
}
