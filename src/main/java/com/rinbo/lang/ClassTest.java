package com.rinbo.lang;

import org.junit.Test;

public class ClassTest {

    @Test
    public void test44(){
        try {
            Class.forName("com.rinbo.lang.ClassForName");
            System.out.println("#########分割符(上面是Class.forName的加载过程，下面是ClassLoader的加载过程)##########");
            ClassLoader.getSystemClassLoader().loadClass("com.test.mytest.ClassForName");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
