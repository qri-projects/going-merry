package com.ggemo.va.goingmerry.op.step;

public interface MmConditionGenerator<Condition, Context> {
    Condition gen(Context context);
}
