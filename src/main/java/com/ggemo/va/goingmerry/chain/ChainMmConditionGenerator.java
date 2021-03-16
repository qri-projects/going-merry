package com.ggemo.va.goingmerry.chain;

/**
 * <p>GmStep相关设计见docs/gm-design.md
 */
public interface ChainMmConditionGenerator<Condition, Context> {
    Condition gen(Context context);
}
