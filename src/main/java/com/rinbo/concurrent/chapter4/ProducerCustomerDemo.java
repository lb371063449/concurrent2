package com.rinbo.concurrent.chapter4;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerCustomerDemo {
    //计数器，多个线程同时访问
    private static AtomicInteger count = new AtomicInteger();
    //生产者
    static class Producer implements Runnable {
        private volatile boolean isRunning = true;
        private BlockingQueue<Data> queue;
        private static final int SLEEPTIME =1000;
        public Producer(BlockingQueue<Data> queue) {
            this.queue = queue;
        }
        public void run() {
            Data data ;
            Random random = new Random();
            System.out.println("start producter id = " + Thread.currentThread().getId());
            while (isRunning) {
                try {
                    Thread.sleep(random.nextInt(SLEEPTIME));
                    data = new Data(count.getAndIncrement());
                    System.out.println("put " + data + " into quene ");
                    if (!queue.offer(data,2, TimeUnit.SECONDS)) {
                        System.out.println("faild to put data " + data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().isInterrupted();
                }
            }
        }

        public void stop() {
            this.isRunning = false;
        }
    }

    //消费者
    static class Customer implements Runnable {
        BlockingQueue<Data> queue;
        private static final int SLEEPTIME =1000;

        public Customer(BlockingQueue<Data> queue) {
            this.queue = queue;
        }

        public void run() {
            Random random = new Random();
            System.out.println("start customer id = " + Thread.currentThread().getId());
            while (true) {
                try {
                    Data data = queue.take();
                    if (data != null) {
                        System.out.println("The result data is : " + data);
                        Thread.sleep(random.nextInt(SLEEPTIME));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().isInterrupted();
                }
            }
        }
    }

    static class Data {
        private final int id;

        Data(int id) {
            this.id = id;
        }

        public String toString() {
            return "Data{id=" + id + '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Data> queue = new LinkedBlockingDeque<>(10);
        ExecutorService es = Executors.newCachedThreadPool();
        Producer p1 = new Producer(queue);
        Producer p2 = new Producer(queue);
        Producer p3 = new Producer(queue);
        Producer p4 = new Producer(queue);
        Producer p5 = new Producer(queue);
        Customer c1 = new Customer(queue);
        Customer c2 = new Customer(queue);
        Customer c3 = new Customer(queue);
        es.execute(p1);
        es.execute(p2);
        es.execute(p3);
        es.execute(p4);
        es.execute(p5);
        es.execute(c1);
        es.execute(c2);
        es.execute(c3);
        Thread.sleep(10000);
        p1.stop();
        p2.stop();
        p3.stop();
        p4.stop();
        p5.stop();
        es.shutdown();
        System.out.println("the end ");
    }
}
