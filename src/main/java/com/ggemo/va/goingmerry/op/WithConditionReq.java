package com.ggemo.va.goingmerry.op;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WithConditionReq<Condition, Req> {
    Condition condition;
    Req req;
}
