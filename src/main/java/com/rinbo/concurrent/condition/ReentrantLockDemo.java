package com.rinbo.concurrent.condition;

public class ReentrantLockDemo {
    public static void main(String[] args) throws InterruptedException {
        Depot depot = new Depot(500);
        new Producer(depot).produce(500,"p1");
        new Producer(depot).produce(200,"p2");
        new Consumer(depot).consume(500,"c1");
        new Consumer(depot).consume(200,"c2");
    }
}
