package com.rinbo.patterns;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.*;
import org.junit.Test;

import java.lang.reflect.Method;

@Slf4j
public class CGLIBTest {

    static class Base {
        public void add() {
            log.debug("add ------------");
        }

        public String sayHello(String name) {
            log.debug("say hello...");
            return "hello " + name;
        }
    }

    static class ConcreteClassNoInterface {
        public String getConcreteMethodA(String str){
            System.out.println("ConcreteMethod A ... "+str);
            return str;
        }
        public int getConcreteMethodB(int n){
            System.out.println("ConcreteMethod B ... "+n);
            return n+10;
        }
        public int getConcreteMethodFixedValue(int n){
            System.out.println("getConcreteMethodFixedValue..."+n);
            return n+10;
        }
    }

    class MICB implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            log.debug("before ------------");
            proxy.invokeSuper(obj, args);
            log.debug("after ------------");
            return null;
        }
    }

    class IHCB implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //此时所有的方法调用时也会返回固定的值Hello Pig,在invoke中，不能使用method.invoke，
            //因为调用的时候又会被拦截，导致死循环
            return "Hello pig";
        }
    }

    //FixedValue拦截器会将所有的方法的返回值替换为固定了一个值
    //被代理的方法不会被执行
    class FVCB implements FixedValue {

        @Override
        public Object loadObject() throws Exception {
            return "22";
        }
    }

    //LazyLoader对象并没有被加载，而是在使用的时候才创建
    class LLCB implements LazyLoader {
        //loadObject只会被调用一次
        @Override
        public Object loadObject() throws Exception {
            System.out.println("创建Person对象");
            Base base = new Base();
            return base;
        }
    }

    class DPCB implements Dispatcher{
        //与LazyLoader区别是，每次都会被调用
        @Override
        public Object loadObject() throws Exception {
            System.out.println("创建Person对象");
            Base base = new Base();
            return base;
        }
    }

    class CFCB implements CallbackFilter{
        @Override
        public int accept(Method method) {
            if("getConcreteMethodB".equals(method.getName())){
                //Callback callbacks[0]
                return 0;
            }else if("getConcreteMethodA".equals(method.getName())){
                //Callback callbacks[1]
                return 1;
            }else if("getConcreteMethodFixedValue".equals(method.getName())){
                //Callback callbacks[2]
                return 2;
            }
            return 1;
        }
    }

    @Test
    public void testMethodInterceptor() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Base.class);
        enhancer.setCallback(new MICB());
        Base base = (Base) enhancer.create();
        base.add();
    }

    @Test
    public void testInvocationHandler() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Base.class);
        enhancer.setCallback(new IHCB());
        Base base = (Base) enhancer.create();
        String result = base.sayHello("rinbo");
        log.debug("result : {}",result);
    }

    @Test
    public void testFixedValue() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Base.class);
        enhancer.setCallback(new FVCB());
        Base base = (Base) enhancer.create();
        String result = base.sayHello("rinbo");
        log.debug("result : {}",result);
    }

    @Test
    public void testLazyLoader() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Base.class);
        Base base  = (Base) Enhancer.create(Base.class,new LLCB());
        System.out.println(base);
        String hello = base.sayHello("aa");
        System.out.println(hello);
        hello = base.sayHello("bb");
        System.out.println(base);
        System.out.println(hello);
    }

    @Test
    public void testDispatcher() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Base.class);
        Base base  = (Base) Enhancer.create(Base.class,new DPCB());
        System.out.println(base);
        String hello = base.sayHello("aa");
        System.out.println(hello);
        hello = base.sayHello("bb");
        System.out.println(base);
        System.out.println(hello);
    }

    @Test
    public void test() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\work\\workspace\\foundation\\src\\main\\java");
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(ConcreteClassNoInterface.class);
        enhancer.setCallbackFilter(new CFCB());
        //(1)
        Callback interceptor=new MICB();
        //(2)
        //什么操作也不做，代理类直接调用被代理的方法不进行拦截
        Callback noOp=NoOp.INSTANCE;
        //(3)
        Callback fixedValue=new FVCB();
        Callback[] callbacks=new Callback[]{interceptor,noOp,fixedValue};
        enhancer.setCallbacks(callbacks);
        ConcreteClassNoInterface proxyObject=(ConcreteClassNoInterface)enhancer.create();

        System.out.println("*** NoOp Callback ***");
        proxyObject.getConcreteMethodA("abcde");

        System.out.println("*** MethodInterceptor Callback ***");
        proxyObject.getConcreteMethodB(1);

        System.out.println("*** FixedValue Callback ***");
        int fixed1=proxyObject.getConcreteMethodFixedValue(128);
        System.out.println("fixedValue1:"+fixed1);
        int fixed2=proxyObject.getConcreteMethodFixedValue(256);
        System.out.println("fixedValue2:"+fixed2);
    }
}
