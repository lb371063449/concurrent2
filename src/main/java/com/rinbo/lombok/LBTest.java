package com.rinbo.lombok;

public class LBTest {
    public static void main(String[] args) {
        Bean bean = new Bean();
        bean.getAge();
        bean.setAge(11);
        System.out.println(bean);
    }
}
