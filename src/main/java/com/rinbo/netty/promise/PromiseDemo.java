package com.rinbo.netty.promise;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.junit.Test;

public class PromiseDemo {

    @Test
    public void testSuccess() {
        final DefaultEventExecutor wang = new DefaultEventExecutor();
        final DefaultEventExecutor li = new DefaultEventExecutor();
        wang.execute(() -> System.out.println(Thread.currentThread().getName() + " 1. 这是一道简单的题"));

        wang.execute(() -> {
            final Promise<Integer> promise = wang.newPromise();
            promise.addListener((Future<? super Integer> future) -> System.out.println(Thread.currentThread().getName() + "复杂题执行结果"));
            li.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 2. 这是一道复杂的题");
                promise.setSuccess(10);
            });
        });

        wang.execute(() -> System.out.println(Thread.currentThread().getName() + " 3. 这是一道简单的题"));
    }

    @Test
    public void testFailue() {
        final DefaultEventExecutor wang = new DefaultEventExecutor();
        final DefaultEventExecutor li = new DefaultEventExecutor();
        wang.submit(() -> System.out.println(Thread.currentThread().getName() + " 1. 这是一道简单的题"));

        wang.submit(() -> {
                Future<String> result = li.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + " 2. 这是一道复杂的题");
                    return null;
                });
                result.addListener((Future<? super String> future)-> System.out.println(Thread.currentThread().getName() + "3. 复杂题执行结果"));
            }
        );
        wang.submit(() -> System.out.println(Thread.currentThread().getName() + " 3. 这是一道简单的题"));
    }
}
