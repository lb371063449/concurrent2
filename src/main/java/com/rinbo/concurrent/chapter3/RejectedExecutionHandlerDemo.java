package com.rinbo.concurrent.chapter3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RejectedExecutionHandlerDemo {

    public static void main(String[] args) {
        ExecutorService es = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(10),
                //自定义线程工厂
                (r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    System.out.println("Thread has created");
                    return t;
                },
                //自定义抛弃策略
                (r, executor) -> {
                    System.out.println(r.toString() + " : discard");
                }
        ) {
            //扩展线程池
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("beforeExecute.........");
            }
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("afterExecute.........");
            }
            @Override
            protected void terminated() {
                System.out.println("terminated.........");
            }
        };
        for (int i = 0; i < 10; i++) {
            es.execute(() -> {
                System.out.println(Thread.currentThread().getId());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        es.shutdown();
    }
}
