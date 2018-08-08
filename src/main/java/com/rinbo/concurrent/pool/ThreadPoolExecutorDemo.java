package com.rinbo.concurrent.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorDemo {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2,2,1000,TimeUnit.SECONDS,new ArrayBlockingQueue<>(12));
        for (int i=0;i<5;i++) {
            try{
                executor.execute(new Task());
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        executor.shutdownNow();
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is running");
        }
    }
}
