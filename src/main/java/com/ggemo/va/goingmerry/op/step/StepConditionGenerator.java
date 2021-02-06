package com.ggemo.va.goingmerry.op.step;

public interface StepConditionGenerator<Condition, Context> {
    Condition generate(Context context);
}
