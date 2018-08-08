package com.rinbo.string;

import org.junit.Test;

public class CharDemo {

    @Test
    public void test1() {
        char c = ' ';
        int i_c = c;
        char b = ' ';
        System.out.println(i_c);
    }

    @Test
    public void test2() {
        for (int i = 0; i < 300; i++) {
            char c = (char) i;
            System.out.println("i = " + i + " , c = "+c);
        }
    }
}
