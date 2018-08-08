package com.rinbo.concurrent.chapter3;

public class InterruptDemo implements Runnable {

    @Override
    public void run() {
        synchronized (this) {
            System.out.println("aa");
        }
    }

    public static void main(String[] args) {
        InterruptDemo demo = new InterruptDemo();
        Thread t1 = new Thread(demo);
        Thread t2 = new Thread(demo);
        t1.start();
        t2.start();

    }
}
