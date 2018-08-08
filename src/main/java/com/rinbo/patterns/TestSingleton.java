package com.rinbo.patterns;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.Constructor;

public class TestSingleton {
    static class Elvis implements Serializable {
        public static final Elvis INSTANCE = new Elvis();

        private Elvis() {
        }

        public void leaveTheBuilding() {
            System.out.println("hello");
        }
    }

    static class Elvis2 {
        public static final Elvis2 INSTANCE = new Elvis2();

        private Elvis2() {
            System.err.println("Elvis Constructor is invoked!");
            if (INSTANCE != null) {
                System.err.println("实例已存在，无法初始化！");
                throw new UnsupportedOperationException("实例已存在，无法初始化！");
            }
        }

    }

    @Test
    public void testReflection() throws Exception {
        Elvis elvis1 = Elvis.INSTANCE;
        Elvis elvis2 = Elvis.INSTANCE;

        System.out.println("elvis1 == elvis2 ? ===>" + (elvis1 == elvis2));
        System.err.println("-----------------");

        /**
         * 反射调用构造方法
         */
        Class clazz = Elvis.class;
        Constructor cons = clazz.getDeclaredConstructor(null);
        cons.setAccessible(true);

        Elvis elvis3 = (Elvis) cons.newInstance(null);

        System.out.println("elvis1 == elvis3 ? ===> "
                + (elvis1 == elvis3));
    }

    @Test
    public void testReflection2() throws Exception {
        Elvis2 elvis1 = Elvis2.INSTANCE;
        Elvis2 elvis2 = Elvis2.INSTANCE;

        System.out.println("elvis1 == elvis2 ? ===>" + (elvis1 == elvis2));
        System.err.println("-----------------");

        Class clazz = Elvis2.class;
        Constructor cons = clazz.getDeclaredConstructor(null);
        cons.setAccessible(true);

        Elvis2 elvis3 = (Elvis2) cons.newInstance(null);

        System.out.println("elvis1 == elvis3 ? ===> "
                + (elvis1 == elvis3));
    }

    @Test
    public void testSerialization() throws Exception {
        Elvis elvis1 = Elvis.INSTANCE;
        FileOutputStream fos = new FileOutputStream("a.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(elvis1);
        oos.flush();
        oos.close();

        Elvis elvis2 = null;
        FileInputStream fis = new FileInputStream("a.txt");
        ObjectInputStream ois = new ObjectInputStream(fis);
        elvis2 = (Elvis) ois.readObject();

        System.out.println("elvis1 == elvis2 ? ===>" + (elvis1 == elvis2));
    }
}
