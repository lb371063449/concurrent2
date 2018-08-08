package com.rinbo.lang.innerclass;

public class HiddenTest {
    //而且由于InsideClass是private的,可以很好的实现隐藏
    //即外部类不知道内部类的具体实现
    private class InsideClass implements InterfaceTest {
        @Override
        public void test() {
            System.out.println("这是一个测试");
        }
    }

    public InterfaceTest getIn() {
        return new InsideClass();
    }

    public static void main(String args[]) {
        HiddenTest a = new HiddenTest();
        InterfaceTest a1 = a.getIn();
        a1.test();
    }
}

interface InterfaceTest {
    void test();
}
