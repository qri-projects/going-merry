package com.ggemo.va.goingmerry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.Proxy;


public class TestProxyBuilder {
    public interface Service {
        String getNiubi();
    }

    public class ServiceInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(new MethodInterceptor() {
                                 @Override
                                 public Object intercept(Object o, Method method, Object[] objects,
                                                         MethodProxy methodProxy)
                                         throws Throwable {
                                     return null;
                                 }
                             }
        );
        Service s = (Service) enhancer.create();
        System.out.println(s.getClass().equals(Service.class));
        System.out.println(s instanceof Service);
        System.out.println(s.getClass().getName());


        Service s2 = (Service) Proxy.newProxyInstance(Service.class.getClassLoader(), new Class[] {Service.class},
                new org.springframework.cglib.proxy.InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

                        return null;
                    }
                });
        System.out.println(s2.getClass().equals(Service.class));
        System.out.println(s2 instanceof Service);
        System.out.println(s2.getClass().getName());
    }
}
