package com.rinbo.zookeeper;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ExpiraTest {
    private static final long expirationInterval = 3000;

    private long roundToInterval(long time) {
        // We give a one interval grace period
        return (time / expirationInterval + 1) * expirationInterval;
    }

    @Test
    public void testExpira() throws InterruptedException {
        CountDownLatch ctd = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    System.out.println(System.currentTimeMillis() + "  :  " + roundToInterval(System.currentTimeMillis()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ctd.countDown();
            }
        }.start();
        ctd.await();
    }
}
