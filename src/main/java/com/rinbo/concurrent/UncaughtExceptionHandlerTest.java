package com.rinbo.concurrent;

import org.junit.Test;

import java.util.concurrent.*;

public class UncaughtExceptionHandlerTest {

    @Test
    public void test1() {
        try {
            new Thread(() -> {
                System.out.println(3 / 2);
                //即使+try cache,出现异常会终止当前线程
                System.out.println(3 / 0);
                System.out.println(3 / 1);
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void test2() {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(() -> {
            Throwable thrown = null;
            try {
                System.out.println(3 / 2);
                System.out.println(3 / 0);
                System.out.println(3 / 1);
            } catch (Throwable e) {
                thrown = e;
            } finally {
                System.out.println("==Exception: " + thrown.getMessage());
            }
        });
        exec.shutdown();
    }

    @Test
    public void test3() throws InterruptedException {
        CountDownLatch ctd = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            System.out.println(3 / 2);
            System.out.println(3 / 0);
            System.out.println(3 / 1);
        });
        //
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("==Exception: " + e.getMessage());
            ctd.countDown();
        });
        thread.start();
        ctd.await();
    }

    @Test
    public void test4() {
        ExecutorService exec = Executors.newCachedThreadPool();
        Thread thread = new Thread(() -> {
            System.out.println(3 / 2);
            System.out.println(3 / 0);
            System.out.println(3 / 1);
        });
        //如果采用线程池通过execute的方法去捕获异常
        //这时需要将异常的捕获封装到Runnable或者Callable中
        //下面这种情况不能捕获异常
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("==Exception: " + e.getMessage());
        });
        exec.execute(thread);
        exec.shutdown();
    }

    @Test
    public void test5() {
        ExecutorService exec = Executors.newCachedThreadPool();
        Thread thread = new Thread(() -> {
            //如果采用线程池通过execute的方法去捕获异常
            //这时需要将异常的捕获封装到Runnable或者Callable中
            //下面这种情况不能捕获异常
            Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
                System.out.println("==Exception: " + e.getMessage());
            });
            System.out.println(3 / 2);
            System.out.println(3 / 0);
            System.out.println(3 / 1);
        });
        exec.execute(thread);
        exec.shutdown();
    }

    @Test
    public void test6() {
        ExecutorService exec = Executors.newCachedThreadPool();
        Thread thread = new Thread(() -> {
            System.out.println(3 / 2);
            System.out.println(3 / 0);
            System.out.println(3 / 1);
        });
        //通过submit提交的任务，无论是抛出的未检测异常还是已检查异常，都将被认为是任务返回状态的一部分。
        //如果一个由submit提交的任务由于抛出了异常而结束，那么这个异常将被Future.get封装在ExecutionException中重新抛出
        Future<?> future = exec.submit(thread);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("==Exception: " + e.getMessage());
        }
        exec.shutdown();
    }
}


