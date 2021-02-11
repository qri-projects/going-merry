package com.ggemo.va.goingmerry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SomeTest {
    static enum ProductLine {
        KA, SME
    }

    public static void main(String[] args) {
        ProductLine productLine = null;
        System.out.println(productLine instanceof ProductLine);
    }
}
