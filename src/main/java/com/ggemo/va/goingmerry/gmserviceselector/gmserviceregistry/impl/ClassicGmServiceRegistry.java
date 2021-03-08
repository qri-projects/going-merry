package com.ggemo.va.goingmerry.gmserviceselector.gmserviceregistry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ggemo.va.goingmerry.exception.SelectServiceException;
import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.gmserviceselector.conditionanalyzer.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.gmserviceselector.gmserviceregistry.GmServiceRegistry;
import com.ggemo.va.goingmerry.utiils.ApplicationContextUtil;
import com.ggemo.va.handler.OpHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>{@link GmServiceRegistry}的classic实现
 */
@Component
public class ClassicGmServiceRegistry implements GmServiceRegistry<ClassicConditionAnalyseResult>,
        OpHandler<ClassicGmServiceRegistry.Req, GmService<?>> {
    // 存放gmService的map, 取gmService的时候按gmServiceClazz取, 根据analyseResult找权重最高的gmService取
    private static final Map<Class<?>, Map<GmService<?>, List<ClassicConditionAnalyseResult>>>
            GG_GM_SERVICE_HOLDER = new HashMap<>();

    private static final Map<GmService<?>, String> GG_GM_SERVICE_2_NAME = new HashMap<>();

    @Autowired
    private ClassicConditionAnalyzer analyzer;

    private ApplicationContext getApplicationContext() {
        return ApplicationContextUtil.getApplicationContext();
    }

    @Override
    public <S extends GmService<?>> S findService(ClassicConditionAnalyseResult mmAnalyseResult,
                                                  Class<S> serviceClazz) {
        // 存放匹配的gmService. key: gmService, value: 命中的special条件数
        Map<GmService<?>, Map<ClassicConditionAnalyseResult, AtomicInteger>> matchedServices = new HashMap<>();

        if (!GG_GM_SERVICE_HOLDER.containsKey(serviceClazz)) {
            throw new SelectServiceException("No matched service of this class! Confirm that you have defined a "
                    + "service that implements class: " + serviceClazz.getName() + ".");
        }

        // 遍历gmService和它的ggAnalyseResults
        GG_GM_SERVICE_HOLDER.get(serviceClazz).forEach((service, ggAnalyseResults) -> {
            Map<ClassicConditionAnalyseResult, AtomicInteger> analyseResult2WeightMap = new HashMap<>();
            matchedServices.put(service, analyseResult2WeightMap);

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

        // 在所有匹配中选出权重最高的. 用一个list来记录权重最高的gmService, 来发现有两个gmService权重一样且都为最高的情况
        int mostMatchedWeight = -1;
        List<GmService<?>> mostMatchedGmServices = new ArrayList<>();

        // 遍历gmService
        for (GmService<?> matchedGmservice : matchedServices.keySet()) {

            // 该gmService下的conditions中的最高权重
            OptionalInt matchedWeightOptional = matchedServices.get(matchedGmservice)
                    .values()
                    .stream()
                    .mapToInt(AtomicInteger::get)
                    .max();
            if (!matchedWeightOptional.isPresent()) {
                continue;
            }
            int gmServiceMatchedWeight = matchedWeightOptional.getAsInt();

            // 与当前最高权重进行对比
            if (gmServiceMatchedWeight > mostMatchedWeight) {
                mostMatchedGmServices = new ArrayList<>();
                mostMatchedGmServices.add(matchedGmservice);

                mostMatchedWeight = gmServiceMatchedWeight;
            } else if (gmServiceMatchedWeight == mostMatchedWeight) {
                mostMatchedGmServices.add(matchedGmservice);
            }
        }

        if (CollectionUtils.isEmpty(mostMatchedGmServices)) {
            throw new SelectServiceException("No matched service about your condition: " + mmAnalyseResult);
        }

        if (mostMatchedGmServices.size() > 1) {
            List<String> matchedBeanNames = mostMatchedGmServices.stream()
                    .map(GG_GM_SERVICE_2_NAME::get)
                    .collect(Collectors.toList());
            throw new SelectServiceException("Not only one gmService matched your condition. Matched beans: "
                    + matchedBeanNames);
        }
        
        return (S) mostMatchedGmServices.get(0);
    }

    @Override
    public void initRegister() {
        ApplicationContext appC = getApplicationContext();
        for (String beanName : appC.getBeanNamesForType(GmService.class)) {
            GmService<?> service = appC.getBean(beanName, GmService.class);
            register(service, beanName);
        }
    }

    @Override
    public void register(GmService<?> service, String beanName) {
        // 记录service和bean名字的对应关系
        GG_GM_SERVICE_2_NAME.put(service, beanName);

        service.getConditions().forEach(c -> {
            // 解析ggCondition
            ClassicConditionAnalyseResult analyseResult = analyzer.analyse(c);

            // 注册一个gmService要将其所有父类注册上去
            Set<Class<?>> registerClasses = getServiceSuperClasses(service.getClass());
            for (Class<?> serviceClazz : registerClasses) {

                // 将analyseResult, gmService放进map中
                if (!GG_GM_SERVICE_HOLDER.containsKey(serviceClazz)) {
                    GG_GM_SERVICE_HOLDER.put(serviceClazz, new HashMap<>());
                }
                if (!GG_GM_SERVICE_HOLDER.get(serviceClazz).containsKey(service)) {
                    GG_GM_SERVICE_HOLDER.get(serviceClazz).put(service, new ArrayList<>());
                }
                GG_GM_SERVICE_HOLDER.get(serviceClazz).get(service).add(analyseResult);
            }
        });
    }

    /**
     * <p>作为handler提供的方法
     */
    @Override
    public GmService<?> handle(Req req) {
        return this.findService(req.getAnalyseResult(), req.getMmServiceClazz());
    }

    @Data
    @AllArgsConstructor
    public static class Req {
        Class<? extends GmService<?>> mmServiceClazz;
        ClassicConditionAnalyseResult analyseResult;
    }

    /**
     * <p>工具方法, 找到给定类的所有继承自{@link GmService}的接口和父类
     *
     * @param clazz 给定类
     * @return 所有父类和所有接口
     */
    private static Set<Class<?>> getServiceSuperClasses(Class<?> clazz) {
        Set<Class<?>> res = new HashSet<>();

        // 递归的出口
        if (clazz == null) {
            return res;
        }
        if (clazz.equals(GmService.class)) {
            res.add(GmService.class);
            return res;
        }

        // 遍历所有上面一层的类和接口
        for (Class<?> superClazzes : getInterfacesAndSuperClass(clazz)) {
            // 递归地获取其所有继承自GmService的接口和父类
            Set<Class<?>> superRes = getServiceSuperClasses(superClazzes);
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
     *
     * @see #getServiceSuperClasses
     */
    private static Set<Class<?>> getInterfacesAndSuperClass(Class<?> clazz) {
        Set<Class<?>> set = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        set.add(clazz.getSuperclass());
        return set;
    }
}
