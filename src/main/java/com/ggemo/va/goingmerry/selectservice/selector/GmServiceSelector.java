package com.ggemo.va.goingmerry.selectservice.selector;

import com.ggemo.va.goingmerry.annotation.GmService;
import com.ggemo.va.goingmerry.selectservice.selector.impl.ClassicGmServiceSelector;

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
     * @param mmCondition    condition, 详细解释见
     *                       <a href="https://github.com/qri-projects/going-merry/blob/master/docs/by-talk.md#ggcondition--mmcondition">文档: 杂谈</a>
     * @return {@link GmService}具体实现类
     */
    <Condition, S extends GmService<Condition>> S select(Class<S> gmServiceClazz, Condition mmCondition);
}
