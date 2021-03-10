package com.ggemo.va.goingmerry.chain;

/**
 * <p>GmStep相关设计见docs/gm-design.md
 */
public interface MmConditionGenerator<Condition, Context> {
    Condition gen(Context context);
}
