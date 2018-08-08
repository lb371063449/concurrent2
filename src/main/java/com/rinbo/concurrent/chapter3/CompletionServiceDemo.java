package com.rinbo.concurrent.chapter3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class CompletionServiceDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        case3();
    }

    /**
     *
     * 1. 用List收集任务结果 (List记录每个submit返回的Future)
     * 2. 循环查看结果, Future不一定完成, 如果没有完成, 那么调用get会租塞
     * 3. 如果排在前面的任务没有完成, 那么就会阻塞, 这样后面已经完成的任务就没法获得结果了, 导致了不必要的等待时间.
     * 更为严重的是: 第一个任务如果几个小时或永远完成不了, 而后面的任务几秒钟就完成了, 那么后面的任务的结果都将得不到处理
     * 导致: 已完成的任务可能得不到及时处理
     */
    private static void case1() throws ExecutionException, InterruptedException {
        final Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<String>> list = new ArrayList<>();
        for (int i=0;i<50;i++) {
            Future<String> future = executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    Thread.sleep(random.nextInt(500));
                    return Thread.currentThread().getName();
                }
            });
            //将执行结果添加到结果列表list中
            list.add(future);
        }
        int count = 0;
        System.out.println("handle result begin");
        //循环结构列表
        for(Future<String> future : list) {
            System.out.println(future.get());
            count++;
        }
        System.out.println("handle result end");
        System.out.println(count + " task done !");
        //关闭线程池
        executorService.shutdown();
    }

    /**
     * 只对第一种情况进行的改进
     * 1. 查看任务是否完成, 如果完成, 就获取任务的结果, 让后重任务列表中删除任务.
     * 2. 如果任务未完成, 就跳过此任务, 继续查看下一个任务结果.
     * 3. 如果到了任务列表末端, 那么就从新回到任务列表开始, 然后继续从第一步开始执行
     * 这样就可以及时处理已完成任务的结果了
     */
    private static void case2() throws ExecutionException, InterruptedException {
        final Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<String>> list = new ArrayList<>();
        for (int i=0;i<50;i++) {
            Future<String> future = executorService.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    Thread.sleep(random.nextInt(500));
                    return Thread.currentThread().getName();
                }
            });
            //将执行结果添加到结果列表list中
            list.add(future);
        }
        int count = 0;
        for(int i=0; i<list.size(); i++) {
            Future<String> taskHolder = list.get(i);
            if(taskHolder.isDone()) { //任务完成
                String result = taskHolder.get(); //获取结果, 进行某些操作
                System.out.println("result: " + result);
                list.remove(taskHolder);
                i--;//由于移除了元素，i也相应--
                count++; //完成的任务的计数器
            }
            //回到列表开头, 从新获取结果
            if(i == list.size() - 1) {
                i = -1;
            }
        }
        System.out.println("handle result end");
        System.out.println(count + " task done !");
        //线程池使用完必须关闭
        executorService.shutdown();
    }

    /**
     * 使用ExecutorCompletionService管理异步任务
     * 1. Java中的ExecutorCompletionService<V>本身有管理任务队列的功能
     *    i. ExecutorCompletionService内部维护列一个队列, 用于管理已完成的任务
     *    ii. 内部还维护列一个Executor, 可以执行任务
     * 2. ExecutorCompletionService内部维护了一个BlockingQueue, 只有完成的任务才被加入到队列中
     * 3. 任务一完成就加入到内置管理队列中, 如果队列中的数据为空时, 调用take()就会阻塞 (等待任务完成)
     *    i. 关于完成任务是如何加入到完成队列中的, 请参考ExecutorCompletionService的内部类QueueingFuture的done()方法
     * 4. ExecutorCompletionService的take/poll方法是对BlockingQueue对应的方法的封装, 关于BlockingQueue的take/poll方法:
     *    i. take()方法, 如果队列中有数据, 就返回数据, 否则就一直阻塞;
     *    ii. poll()方法: 如果有值就返回, 否则返回null
     *    iii. poll(long timeout, TimeUnit unit)方法: 如果有值就返回, 否则等待指定的时间; 如果时间到了如果有值, 就返回值, 否则返回null
     */
    private static void case3() throws ExecutionException, InterruptedException {
        final Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ExecutorCompletionService<String> completion = new ExecutorCompletionService<>(executorService);
        for (int i=0;i<50;i++) {
           completion.submit(new Callable<String>() {
               @Override
               public String call() throws Exception {
                   Thread.sleep(random.nextInt(5000));
                   return Thread.currentThread().getName();
               }
           });
        }
        int count = 0;
        System.out.println("handle result start");
        while (count<50) {
            //ExecutorCompletionService内部维护了一个BlockingQueue, 只有完成的任务才被加入到队列中
            Future<String> future = completion.take();
            System.out.println("result: " + future.get());
            count++;
        }
        System.out.println("handle result end");
        System.out.println(count + " task done !");
        //线程池使用完必须关闭
        executorService.shutdown();
    }
}
