package com.rinbo.concurrent.chapter3;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchDemo implements Runnable{
    //表示需要有10个线程完成任务，等待在CountDownLatch上的线程才能继续执行
    CountDownLatch end = new CountDownLatch(10);

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(10)*1000);
            System.out.println("Check complete ！");
            //一个线程完成任务，调用计数器countDown方法减一
            end.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchDemo dem = new CountDownLatchDemo();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i=0;i<20;i++) {
            executor.submit(dem);
        }
        //等待检查，也就是CountDownLatch需要的线程个数都执行完成,主线程才继续执行
        dem.end.await();
        System.out.println("Fire !");
        executor.shutdown();
    }
}
