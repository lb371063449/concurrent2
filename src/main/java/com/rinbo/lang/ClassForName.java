package com.rinbo.lang;

public class ClassForName {
    static {
        System.out.println("执行了静态代码块");
    }

    private static String staticFiled = staticMethod();

    public static String staticMethod() {
        System.out.println("执行了静态方法");
        return "给静态字段赋值了";
    }
}