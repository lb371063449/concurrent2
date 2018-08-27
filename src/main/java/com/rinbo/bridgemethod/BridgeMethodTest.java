package com.rinbo.bridgemethod;

import java.lang.reflect.Method;

public class BridgeMethodTest {

    public static void main(String[] args) {
        C c = new D();
        Method[] methods = D.class.getMethods();
        for (Method method:methods) {
            System.out.println(method.getParameterTypes());
        }
    }
}
