package com.rinbo.lang.innerclass;

public class InnerBasicTest {
}

class OuterClass {
    public void display() {
        System.out.println("OuterClass...");
    }

    public class InnerClass {
        public OuterClass getOuterClass() {
            //.this返回外部类的引用
            return OuterClass.this;
        }
    }

    public static void main(String[] args) {
        OuterClass outerClass = new OuterClass();
        //.new根据外部类对象创建内部类
        OuterClass.InnerClass innerClass = outerClass.new InnerClass();
        innerClass.getOuterClass().display();
    }
}

class StaticOuterClass {
    private String sex;
    public static String name = "chenssy";

    static class InnerClass1 {
        //在静态内部类中可以存在静态成员
        public static String _name1 = "chenssy_static";

        public void display() {
            //静态内部类只能访问外围类的静态成员变量和方法
            System.out.println("OutClass name :" + name);
        }
    }

    class InnerClass2 {
        //非静态内部类中不能存在静态成员
        public String _name2 = "chenssy_inner";

        //非静态内部类中可以调用外围类的任何成员,不管是静态的还是非静态的
        public void display() {
            System.out.println("OuterClass name：" + name);
        }
    }

    public void display() {
        //直接访问静态内部类：内部类.
        System.out.println(InnerClass1._name1);
        //可以直接创建实例不需要依赖于外围类
        new InnerClass1().display();

        //非静态内部的创建需要依赖于外围类
        StaticOuterClass.InnerClass2 inner2 = new StaticOuterClass().new InnerClass2();
        //方位非静态内部类的成员需要使用非静态内部类的实例
        System.out.println(inner2._name2);
        inner2.display();
    }

    public static void main(String[] args) {
        StaticOuterClass outer = new StaticOuterClass();
        outer.display();
    }
}