package com.rinbo.bit;

public class ORComputeDemo {

    public static void main(String[] args) {
        System.out.println(22 | 0);
        int COUNT_BITS = Integer.SIZE - 3;
        int CAPACITY   = (1 << COUNT_BITS) - 1;
        System.out.println(CAPACITY);
        int RUNNING    = -1 << COUNT_BITS;
        System.out.println(RUNNING);
        int SHUTDOWN   =  0 << COUNT_BITS;
        System.out.println(SHUTDOWN);
    }
}
