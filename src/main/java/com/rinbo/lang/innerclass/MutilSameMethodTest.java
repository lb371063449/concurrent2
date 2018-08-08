package com.rinbo.lang.innerclass;

public class MutilSameMethodTest {

    private class IF1 implements F1 {

        @Override
        public void sayHello() {
            System.out.println("IF1 sayHello");
        }
    }

    private class IM1 extends M1 {}

    public void iSay() {
        //调用接口的sayHello()
        new IF1().sayHello();
    }

    public void cSay() {
        //调用类的sayHello()
        new IM1().sayHello();
    }
}

interface F1 {
    void sayHello();
}

class M1 implements F1 {

    @Override
    public void sayHello() {
        System.out.println("M1 sayHello");
    }
}