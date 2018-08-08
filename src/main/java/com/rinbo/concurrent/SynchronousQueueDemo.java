package com.rinbo.concurrent;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {

    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<>(false);
        Producer p1 = new Producer("p1", queue, 10);
        Producer p2 = new Producer("p2", queue, 50);

        Consumer c1 = new Consumer("c1", queue);
        Consumer c2 = new Consumer("c2", queue);

        c1.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        p1.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        p2.start();
    }

    /**
     * 生产者
     */
    static class Producer extends Thread {
        private SynchronousQueue<Integer> queue;
        private int n;
        public Producer(String name, SynchronousQueue<Integer> queue, int n) {
            super(name);
            this.queue = queue;
            this.n = n;
        }

        @Override
        public void run() {
            System.out.println(getName() + " offer result " + queue.offer(n));
        }
    }

    /**
     * 消费者
     */
    static class Consumer extends Thread {
        private SynchronousQueue<Integer> queue;
        public Consumer(String name, SynchronousQueue<Integer> queue) {
            super(name);
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                System.out.println(getName() + " take result " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
