package com.rinbo.patterns;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InterfaceMakerTest {
    static class ConcreteClassNoInterface {
        public String getConcreteMethodA(String str) {
            System.out.println("ConcreteMethod A ... " + str);
            return str;
        }

        public int getConcreteMethodB(int n) {
            System.out.println("ConcreteMethod B ... " + n);
            return n + 10;
        }

        public int getConcreteMethodFixedValue(int n) {
            System.out.println("getConcreteMethodFixedValue..." + n);
            return n + 10;
        }
    }

    @Test
    public void testInterfaceMaker() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InterfaceMaker im = new InterfaceMaker();
        im.add(ConcreteClassNoInterface.class);
        Class interfaceOjb = im.create();
        //返回的是接口
        System.out.println(interfaceOjb.isInterface());
        //net.sf.cglib.empty.Object$$InterfaceMakerByCGLIB$$13e205f
        System.out.println(interfaceOjb.getName());

        //返回与ConcreteClassNoInterface类内定义的方法完全相同
        Method[] methods = interfaceOjb.getMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
        //通过生成的接口，可以对某个类进行Enhancer
        Object obj = Enhancer.create(Object.class, new Class[]{interfaceOjb},
                new MethodInterceptor() {
                    @Override
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        return "intercept!";
                    }
                });

        Method method = obj.getClass().getMethod("getConcreteMethodA", new Class[]{String.class});
        System.out.println(method.invoke(obj, new Object[]{"12345"}));
    }
}
