package com.rinbo.concurrent.chapter4;

public class FalseShare implements Runnable{

    //线程数、数组大小：
    public static int NUM_THREADS = 4; // change

    //数组迭代的次数：
    public final static long ITERATIONS = 500L * 1000L * 1000L;

    //线程需要处理的数组元素角标：
    private final int handleArrayIndex;

    //操作数组：
    private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];

    //对数组的元素进行赋值：
    static{
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
    }

    public FalseShare(final int handleArrayIndex) {
        this.handleArrayIndex = handleArrayIndex;
    }

    //启动线程，每一个线程操作一个数组的元素，一一对应：
    public static void main(final String[] args) throws Exception {
        //程序睡眠必须加上：
        Thread.sleep(10000);

        final long start = System.nanoTime();

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseShare(i));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println(System.nanoTime() - start);
    }

    //对数组的元素进行操作：
    public void run() {
        long i = ITERATIONS;
        while (0 != --i) {
            longs[handleArrayIndex].value = i;
        }
    }

    //数组元素：
    public final static class VolatileLong {
        public volatile long value = 0L;
        public long p1, p2, p3, p4, p5; //代码1
        public int p6;//代码1
    }
}
