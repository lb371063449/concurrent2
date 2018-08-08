package com.rinbo.concurrent.chapter3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemapDemo implements Runnable {
    private Semaphore semap = new Semaphore(5);
    @Override
    public void run() {
        try {
            semap.acquire();
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getId()+" : done!");
            semap.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SemapDemo semapDemo = new SemapDemo();
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i=0;i<20;i++) {
            service.submit(semapDemo);
        }
    }
}
