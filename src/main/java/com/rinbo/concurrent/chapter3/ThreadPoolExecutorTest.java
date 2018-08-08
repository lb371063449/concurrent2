package com.rinbo.concurrent.chapter3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Executor代表一个线程池，它的子类有ExecutorService、ThreadPoolExecutor，Executors代表线程池工厂
public class ThreadPoolExecutorTest {
    public static void main(String[] args) {
//        newCachedThreadPool();
//        newFixedThreadPool();
        newScheduledThreadPool();
//        newSingleThreadExecutor();
    }
    //
    //newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
    //线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
    public static void newCachedThreadPool() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i=0;i<10;i++) {
            final int index = i;
            try {
                Thread.sleep(index * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "=====" + index);
                }
            });
        }
    }

    //newFixedThreadPool创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待，
    //因为线程池大小为3，每个任务输出index后sleep 2秒，所以每两秒打印3个数字。
    public static void newFixedThreadPool() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i=0;i<10;i++) {
            final int index = i;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "=====" + index);
                }
            });
        }
    }

    //newScheduledThreadPool创建一个定长线程池，支持定时及周期性任务执行
    public static void newScheduledThreadPool() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
        //表示延迟1秒后每3秒执行一次
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "   delay 3 seconds");
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    //newSingleThreadExecutor创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，
    //保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
    public static void newSingleThreadExecutor() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "=====" + index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}