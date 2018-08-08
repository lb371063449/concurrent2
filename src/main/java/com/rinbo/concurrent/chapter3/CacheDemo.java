package com.rinbo.concurrent.chapter3;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;


public class CacheDemo {
    public static void main(String[] args) {
        final Memoizer<String,BigInteger> memoizerl = new Memoizer(new ExpensiveFunction());
        for (int i=0;i<1000;i++) {
            int finalI = i;
            new Thread(){
               @Override
               public void run() {
                   try {
                       BigInteger value = memoizerl.compute(new Random().nextInt(100) + "");
                       System.out.println("i = : " + finalI + " ,value = " + value);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   }
               }
           }.start();
        }
    }
}

interface Computable<A,V> {
    V compute(A a) throws InterruptedException, ExecutionException;
}

class ExpensiveFunction implements Computable<String,BigInteger> {

    //假设这个方法要执行很久
    @Override
    public BigInteger compute(String s) throws InterruptedException {
        Thread.sleep(500);
        return new BigInteger(s);
    }
}
//4、比较接近完美的方案
class Memoizer<A,V> implements Computable<A,V> {
    //使用HashMap做缓存
    private final Map<A,Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A,V> c;

    public Memoizer(Computable<A,V> c) {
        this.c = c;
    }

    @Override
    public V compute(A a) throws InterruptedException, ExecutionException {
        //这个无限循环用户取消FutureTask情况
        while (true) {
            //首先从缓存中获取
            Future<V> result = cache.get(a);
            //如果获取不到
            if (result == null) {
                //构造FutureTask
                FutureTask<V> futureTask = new FutureTask<>(new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return c.compute(a);
                    }
                });
                //如果不存在则新增
                result = cache.putIfAbsent(a,futureTask);
                if (result == null) {
                    result = futureTask;
                    futureTask.run();
                }
            }
            try {
                return result.get();
            } catch (CancellationException e){
                //如果取消了FutureTask，则从缓存中异常
                cache.remove(a,result);
            }
            catch (ExecutionException e) {
                throw e;
            }
        }
    }
}
//3、由于compute方法使用了异步计算，因此不会存在计算阻塞情况，但是可能会两个线程计算出相同的值
class Memoizer3<A,V> implements Computable<A,V> {
    //使用HashMap做缓存
    private final Map<A,Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A,V> c;

    public Memoizer3(Computable<A,V> c) {
        this.c = c;
    }

    @Override
    public V compute(A a) throws InterruptedException, ExecutionException {
        Future<V> result = cache.get(a);
        if (result == null) {
            Callable<V> callable = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return c.compute(a);
                }
            };
            FutureTask<V> futureTask = new FutureTask<>(callable);
            result = futureTask;
            cache.put(a,futureTask);
            //在这里调用FutureTask的run方法
            futureTask.run();
        }
        try {
            return result.get();
        } catch (ExecutionException e) {
            throw e;
        }
    }
}
//2、由于Memoizer2使用ConcurrentHashMap(线程安全类),应此移除了其compute方法的同步操作，所有此时Memoizer2不是线程安全的，
//可能会两个线程计算出相同的值
class Memoizer2<A,V> implements Computable<A,V> {
    //使用HashMap做缓存
    private final Map<A,V> cache = new ConcurrentHashMap<>();
    private final Computable<A,V> c;

    public Memoizer2(Computable<A,V> c) {
        this.c = c;
    }

    @Override
    public V compute(A a) throws InterruptedException, ExecutionException {
        V result = cache.get(a);
        if (result == null) {
            result = c.compute(a);
            cache.put(a,result);
        } else {
            System.out.println(result);
        }
        return result;
    }
}
class Memoizerl<A,V> implements Computable<A,V> {
    //使用HashMap做缓存
    private final Map<A,V> cache = new HashMap<>();
    private final Computable<A,V> c;

    public Memoizerl(Computable<A,V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A a) throws InterruptedException, ExecutionException {
        V result = cache.get(a);
        if (result == null) {
            result = c.compute(a);
            cache.put(a,result);
        } else {
            System.out.println(result);
        }
        return result;
    }
}
