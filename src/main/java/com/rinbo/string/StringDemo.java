package com.rinbo.string;

import org.junit.Test;

public class StringDemo {

    @Test

    public void test1() {
        String s1 = "hello";
        String s2 = new String("hello");
        System.out.println(s1 == s2);
    }
}
