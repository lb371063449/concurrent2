package com.rinbo.concurrent.chapter3;

import java.util.concurrent.*;

public class TraceThreadPoolExecutor extends ThreadPoolExecutor {
    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    //覆盖线程池执行方法
    @Override
    public void execute(Runnable command) {
        super.execute(Wrap(command, clientTrace(), Thread.currentThread().getName()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(Wrap(task, clientTrace(), Thread.currentThread().getName()));
    }

    //返回异常类
    private Exception clientTrace() {
        return new Exception("client trace ");
    }

    //包装Runnable对象，将异常wrap到线程中
    public Runnable Wrap(Runnable task, Exception exception, String clientName) {
        return new Runnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {
                    //打印异常追踪信息
                    exception.printStackTrace();
                    throw e;
                }
            }
        };
    }

    public static class DivTask implements Runnable {
        int a, b;

        public DivTask(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public void run() {
            double result = a / b;
            System.out.println(result);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 0l, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
        for (int i = 0; i < 5; i++) {
            Future<?> future = executor.submit(new DivTask(100, i));
            future.get();
        }
    }
}

