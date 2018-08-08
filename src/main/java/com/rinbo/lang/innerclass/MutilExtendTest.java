package com.rinbo.lang.innerclass;

public class MutilExtendTest {

    private class test1 extends Example1 {
        @Override
        public String name() {
            return super.name();
        }
    }

    private class test2 extends Example2 {
        @Override
        public int age() {
            return super.age();
        }
    }

    public String name() {
        return new test1().name();
    }

    public int age() {
        return new test2().age();
    }

    public static void main(String args[]) {
        MutilExtendTest mi = new MutilExtendTest();
        System.out.println("姓名:" + mi.name());
        System.out.println("年龄:" + mi.age());
    }
}

class Example1 {
    public String name() {
        return "liutao";
    }
}

class Example2 {
    public int age() {
        return 25;
    }
}