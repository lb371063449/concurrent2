package com.rinbo.concurrent.chapter3;

import java.util.concurrent.*;

public class StackTask {

    public static class DivTask implements Runnable {
        int a,b;
        public DivTask(int a,int b) {
            this.a = a;
            this.b = b;
        }
        public void run() {
            double result = a/b;
            System.out.println(result);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = new ThreadPoolExecutor(0,Integer.MAX_VALUE,0l,TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>());
        for (int i=0;i<5;i++) {
            Future<?> future = executor.submit(new DivTask(100, i));
            future.get();
        }
    }
}
