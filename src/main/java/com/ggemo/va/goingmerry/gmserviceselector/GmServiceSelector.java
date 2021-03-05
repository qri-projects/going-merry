package com.ggemo.va.goingmerry.gmserviceselector;

import com.ggemo.va.goingmerry.gmservice.GmService;
import com.ggemo.va.goingmerry.gmserviceselector.impl.ClassicGmServiceSelector;

/**
 * <p>根据条件找实现类的接口
 *
 * @see ClassicGmServiceSelector
 */
public interface GmServiceSelector {
    /**
     * 根据条件找实现类
     *
     * @param gmServiceClazz handler的类
     * @param mmCondition  // todo: mmCondition文档  condition
     * @return {@link GmService}具体实现类
     */
    <Condition, S extends GmService<Condition>> S select(Class<S> gmServiceClazz, Condition mmCondition);
}
