package com.rinbo.concurrent.chapter3;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    static Object u = new Object();
    public static class ChangeObjectThread extends Thread{
        public ChangeObjectThread(String name) {
            super(name);
        }
        public void run() {
            System.out.println("befor into run");
            synchronized (u) {
                System.out.println("in " + getName());
                //阻塞当前线程，和Thread.suspend相比，不会存在resume在前发生导致线程无法继续执行
                //比wait相比，不需要获得对象锁，也不会抛出interrupted异常
                LockSupport.park();
                //在LockSupport.park中可以响应interrupted事件
                if (Thread.interrupted()) {
                    System.out.println(getName() + " : 线程中段");
                }
                System.out.println(getName() + " : 执行结束 ！");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockSupportDemo demo = new LockSupportDemo();
        Thread t1 = new ChangeObjectThread("T1");
        Thread t2 = new ChangeObjectThread("T2");
        t1.start();
        Thread.sleep(1000);
        //此时会释放LockSupport.park占用的锁
//        t1.interrupt();
        t2.start();
        //释放锁，不用担心unpark方法先执行导致LockSupport.park一直阻塞
        LockSupport.unpark(t1);
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
    }
}
