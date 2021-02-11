package com.ggemo.va.goingmerry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    static class Ji2HandlerImpl extends JjHandlerImpl {

    }

    static Set<Class> getSuperClasses(Class clazz) {
        Set<Class> res = new HashSet<>();


        while (Arrays.asList(clazz.getInterfaces()).contains(OpHandler.class)) {
            res.add(clazz);
            clazz = clazz.getSuperclass();
        }
        return res;
    }

    static Set<Class> getInterfacesAndSuperClass(Class clazz) {
        Set<Class> set = new HashSet<>();
        set.addAll(Arrays.asList(clazz.getInterfaces()));
        set.add(clazz.getSuperclass());
        return set;
    }

    static Set<Class> getHandlerSuperClasses(Class clazz) {
        if (clazz == null) {
            return new HashSet<>();
        }
        if (clazz.equals(OpHandler.class)) {
            return new HashSet<Class>(){{add(OpHandler.class);}};
        }
        Set<Class> res = new HashSet<>();
        for (Class interfaze : getInterfacesAndSuperClass(clazz)) {
            Set<Class> interfazeRes = getHandlerSuperClasses(interfaze);
            if (interfazeRes.size() == 0) {
                continue;
            }
            res.addAll(getHandlerSuperClasses(interfaze));
        }
        if (res.size() != 0) {
            res.add(clazz);
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(getHandlerSuperClasses(Ji2HandlerImpl.class));
    }
}
