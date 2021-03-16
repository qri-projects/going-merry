package com.ggemo.va.goingmerry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ggemo.va.goingmerry.utils.SuperClassUtils;
import com.ggemo.va.handler.OpHandler;

public class SomeTest {
    static enum ProductLine {
        KA, SME
    }

    static interface JjHandler extends OpHandler<String, String>{}
    static interface YYY extends OpHandler<String, String>{}
    static interface XXX {}

    static class JjHandlerImpl implements JjHandler, YYY, XXX {

        @Override
        public String handle(String s) {
            return null;
        }
    }

    static class Ji2HandlerImpl extends JjHandlerImpl implements XXX {

    }

    public static void main(String[] args) {
        System.out.println(SuperClassUtils.getAllSupers(Ji2HandlerImpl.class));
    }





}
