package com.ggemo.va.goingmerry;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SomeTest {
    @Data
    @AllArgsConstructor
    static class A {
        String name;
    }
    public static void main(String[] args) {
        System.out.println(new Long(1).hashCode());
    }
}
