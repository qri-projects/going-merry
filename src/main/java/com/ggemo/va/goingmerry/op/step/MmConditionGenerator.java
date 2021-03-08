package com.ggemo.va.goingmerry.op.step;

/**
 * <p>GmStep相关设计见docs/gm-design.md
 */
public interface MmConditionGenerator<Condition, Context> {
    Condition gen(Context context);
}
