package com.ggemo.va.goingmerry.service.selectservice.serviceregistry.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.ggemo.va.goingmerry.service.base.GmService;
import com.ggemo.va.goingmerry.service.selectservice.condition.analyseResult.impl.ClassicConditionAnalyseResult;
import com.ggemo.va.goingmerry.service.selectservice.condition.analyzer.impl.ClassicConditionAnalyzer;
import com.ggemo.va.goingmerry.service.selectservice.serviceregistry.GmServiceRegistry;
import com.ggemo.va.goingmerry.utils.ApplicationContextUtil;
import com.ggemo.va.goingmerry.utils.SuperClassUtils;
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
        List<GmService<?>> mostMatchedServices = new ArrayList<>();

        // 遍历gmService
        for (GmService<?> matchedService : matchedServices.keySet()) {

            // 该gmService下的conditions中的最高权重
            OptionalInt matchedWeightOptional = matchedServices.get(matchedService)
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
                mostMatchedServices = new ArrayList<>();
                mostMatchedServices.add(matchedService);

                mostMatchedWeight = gmServiceMatchedWeight;
            } else if (gmServiceMatchedWeight == mostMatchedWeight) {
                mostMatchedServices.add(matchedService);
            }
        }

        // 最匹配的只有一个的情况, 返回
        if (mostMatchedServices.size() == 1) {
            return (S) mostMatchedServices.get(0);
        }

        // mostMatchedGmServices为空, 提示找不到service
        if (CollectionUtils.isEmpty(mostMatchedServices)) {
            throw new SelectServiceException("No matched service about your condition: " + mmAnalyseResult);
        }

        // mostMatchedGmServices有多个的情况下

        // 如果权重为0, 代表都不匹配, 提示找不到service
        if (mostMatchedWeight <= 0) {
            throw new SelectServiceException("No matched service about your condition: " + mmAnalyseResult);
        }

        // 权重大于0, 提示冗余匹配
        List<String> matchedBeanNames = mostMatchedServices.stream()
                .map(GG_GM_SERVICE_2_NAME::get)
                .collect(Collectors.toList());
        throw new SelectServiceException("More than one gmServices matched your condition. Matched beans: "
                + matchedBeanNames + ". your condition: " + mmAnalyseResult);

    }

    @Override
    public void initRegister() {
        getApplicationContext().getBeansOfType(GmService.class).forEach(
                (beanName, service) ->
                        register(service, beanName)
        );
    }

    @Override
    public void register(GmService<?> service, String beanName) {
        // 记录service和bean名字的对应关系
        GG_GM_SERVICE_2_NAME.put(service, beanName);

        service.getGgConditions().forEach(c -> {
            // 解析ggCondition
            ClassicConditionAnalyseResult analyseResult = analyzer.analyse(c);

            // 注册一个gmService要将其所有父类注册上去
            Set<Class<? extends GmService>> registerClasses =
                    SuperClassUtils.getServiceSuperClassesUntil(service.getClass(), GmService.class);
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

}
